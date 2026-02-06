package com.koroFoods.paymentService.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koroFoods.paymentService.dtos.ConfirmarPagoRequest;
import com.koroFoods.paymentService.dtos.CrearPagoRequest;
import com.koroFoods.paymentService.dtos.PagoAnuladoEvent;
import com.koroFoods.paymentService.dtos.PagoConfirmadoEvent;
import com.koroFoods.paymentService.dtos.PagoResponse;
import com.koroFoods.paymentService.dtos.QRDataResponse;
import com.koroFoods.paymentService.enums.EstadoPago;
import com.koroFoods.paymentService.enums.MetodoPago;
import com.koroFoods.paymentService.enums.TipoPago;
import com.koroFoods.paymentService.exception.BusinessException;
import com.koroFoods.paymentService.exception.ResourceNotFoundException;
import com.koroFoods.paymentService.messaging.PagoEventPublisher;
import com.koroFoods.paymentService.model.Pago;
import com.koroFoods.paymentService.repository.IPagoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {

	private final IPagoRepository pagoRepository;
    private final PagoEventPublisher eventPublisher;

    // Datos del negocio para Yape/Plin
    private static final String NUMERO_YAPE = "987654321"; // Número de Yape del negocio
    private static final String NUMERO_PLIN = "987654321"; // Número de Plin del negocio
    private static final String NOMBRE_NEGOCIO = "KoroFood Restaurant";

    @Transactional
    public QRDataResponse crearPago(CrearPagoRequest request) {
        validarRequest(request);

        Pago pago = new Pago();
        pago.setIdReserva(request.getIdReserva());
        pago.setIdPedido(request.getIdPedido());
        pago.setIdUsuario(request.getIdUsuario());
        pago.setTipoPago(TipoPago.valueOf(request.getTipoPago()));
        pago.setMonto(request.getMonto());
        pago.setMetodoPago(MetodoPago.valueOf(request.getMetodoPago()));
        pago.setObservaciones(request.getObservaciones());
        pago.setEstado(EstadoPago.PEN);

        // Generar referencia única
        pago.setReferenciaPago(generarReferencia());

        Pago guardado = pagoRepository.save(pago);

        // Retornar datos para generar QR en frontend
        return generarQRData(guardado);
    }

    @Transactional
    public PagoResponse confirmarPago(ConfirmarPagoRequest request) {
        Pago pago = pagoRepository.findById(request.getIdPago())
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + request.getIdPago()));

        // Validar estado
        if (pago.getEstado() != EstadoPago.PEN) {
            throw new BusinessException("El pago ya fue procesado. Estado actual: " + pago.getEstado());
        }

        // Validar expiración
        if (LocalDateTime.now().isAfter(pago.getFechaExpiracion())) {
            pago.setEstado(EstadoPago.EXP);
            pagoRepository.save(pago);
            throw new BusinessException("El pago ha expirado. Por favor, genere uno nuevo.");
        }

        // Validar código de operación único
        if (pagoRepository.existsByCodigoOperacion(request.getCodigoOperacion())) {
            throw new BusinessException("El código de operación ya fue utilizado");
        }

        // Confirmar pago
        pago.setCodigoOperacion(request.getCodigoOperacion());
        pago.setEstado(EstadoPago.PAG);
        pago.setFechaPago(LocalDateTime.now());
        if (request.getObservaciones() != null) {
            pago.setObservaciones(request.getObservaciones());
        }

        Pago confirmado = pagoRepository.save(pago);

        // Publicar evento en RabbitMQ
        publicarEventoPagoConfirmado(confirmado);

        return mapearAResponse(confirmado);
    }

    @Transactional
    public PagoResponse anularPago(Integer idPago, String motivo) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + idPago));

        if (pago.getEstado() == EstadoPago.PAG) {
            throw new BusinessException("No se puede anular un pago ya confirmado");
        }

        pago.setEstado(EstadoPago.ANU);
        pago.setObservaciones(motivo);

        Pago anulado = pagoRepository.save(pago);

        // Publicar evento en RabbitMQ
        publicarEventoPagoAnulado(anulado, motivo);

        return mapearAResponse(anulado);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorUsuario(Integer idUsuario) {
        return pagoRepository.findByIdUsuario(idUsuario).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagoResponse buscarPorId(Integer id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + id));
        return mapearAResponse(pago);
    }

    @Transactional(readOnly = true)
    public PagoResponse buscarPorReferencia(String referencia) {
        Pago pago = pagoRepository.findByReferenciaPago(referencia)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con referencia: " + referencia));
        return mapearAResponse(pago);
    }

    // Métodos privados

    private void validarRequest(CrearPagoRequest request) {
        if (request.getIdReserva() == null && request.getIdPedido() == null) {
            throw new BusinessException("Debe especificar ID de reserva o ID de pedido");
        }

        if (request.getIdReserva() != null && request.getIdPedido() != null) {
            throw new BusinessException("Solo puede especificar ID de reserva O ID de pedido, no ambos");
        }

        // Validar método de pago según tipo
        MetodoPago metodo = MetodoPago.valueOf(request.getMetodoPago());
        if (request.getTipoPago().equals("DR") && metodo == MetodoPago.EFECTIVO) {
            throw new BusinessException("No se acepta efectivo para depósitos de reserva");
        }
    }

    private String generarReferencia() {
        return "KORO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private QRDataResponse generarQRData(Pago pago) {
        String numeroDestino;
        if (pago.getMetodoPago() == MetodoPago.YAPE) {
            numeroDestino = NUMERO_YAPE;
        } else if (pago.getMetodoPago() == MetodoPago.PLIN) {
            numeroDestino = NUMERO_PLIN;
        } else {
            throw new BusinessException("El método de pago " + pago.getMetodoPago() + " no soporta QR");
        }

        String concepto = String.format("KoroFood - Ref: %s", pago.getReferenciaPago());

        // Datos para el QR (formato que entiende Yape/Plin)
        // Formato: yape://pago?numero=XXX&monto=YYY&concepto=ZZZ
        String qrData = String.format(
                "%s://pago?numero=%s&monto=%.2f&concepto=%s",
                pago.getMetodoPago().name().toLowerCase(),
                numeroDestino,
                pago.getMonto(),
                concepto
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return QRDataResponse.builder()
                .idPago(pago.getIdPago())
                .referenciaPago(pago.getReferenciaPago())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago().name())
                .numeroDestino(numeroDestino)
                .nombreDestino(NOMBRE_NEGOCIO)
                .concepto(concepto)
                .qrData(qrData)
                .fechaExpiracion(pago.getFechaExpiracion().format(formatter))
                .build();
    }

    private void publicarEventoPagoConfirmado(Pago pago) {
        PagoConfirmadoEvent event = PagoConfirmadoEvent.builder()
                .idPago(pago.getIdPago())
                .idReserva(pago.getIdReserva())
                .idPedido(pago.getIdPedido())
                .idUsuario(pago.getIdUsuario())
                .tipoPago(pago.getTipoPago().name())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago().name())
                .fechaPago(pago.getFechaPago())
                .codigoOperacion(pago.getCodigoOperacion())
                .build();

        eventPublisher.publicarPagoConfirmado(event);
    }

    private void publicarEventoPagoAnulado(Pago pago, String motivo) {
        PagoAnuladoEvent event = PagoAnuladoEvent.builder()
                .idPago(pago.getIdPago())
                .idReserva(pago.getIdReserva())
                .idPedido(pago.getIdPedido())
                .motivo(motivo)
                .build();

        eventPublisher.publicarPagoAnulado(event);
    }

    private String obtenerDescripcionTipoPago(TipoPago tipo) {
        return switch (tipo) {
            case DR -> "Depósito Reserva";
            case PP -> "Pago Pedido";
        };
    }

    private String obtenerDescripcionMetodoPago(MetodoPago metodo) {
        return switch (metodo) {
            case YAPE -> "Yape";
            case PLIN -> "Plin";
            case EFECTIVO -> "Efectivo";
            case TARJETA -> "Tarjeta";
        };
    }

    private String obtenerDescripcionEstado(EstadoPago estado) {
        return switch (estado) {
            case PEN -> "Pendiente";
            case PAG -> "Pagado";
            case ANU -> "Anulado";
            case EXP -> "Expirado";
        };
    }

    private PagoResponse mapearAResponse(Pago pago) {
        return PagoResponse.builder()
                .idPago(pago.getIdPago())
                .idReserva(pago.getIdReserva())
                .idPedido(pago.getIdPedido())
                .idUsuario(pago.getIdUsuario())
                .tipoPago(pago.getTipoPago().name())
                .tipoPagoDescripcion(obtenerDescripcionTipoPago(pago.getTipoPago()))
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago().name())
                .metodoPagoDescripcion(obtenerDescripcionMetodoPago(pago.getMetodoPago()))
                .fechaPago(pago.getFechaPago())
                .estado(pago.getEstado().name())
                .estadoDescripcion(obtenerDescripcionEstado(pago.getEstado()))
                .observaciones(pago.getObservaciones())
                .referenciaPago(pago.getReferenciaPago())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaExpiracion(pago.getFechaExpiracion())
                .codigoOperacion(pago.getCodigoOperacion())
                .build();
    }
    
    
}
