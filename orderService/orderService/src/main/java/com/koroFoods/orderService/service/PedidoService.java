package com.koroFoods.orderService.service;

import com.koroFoods.orderService.dto.DetallePedidoRequestDTO;
import com.koroFoods.orderService.dto.PedidoResumenDto;
import com.koroFoods.orderService.dto.PedidoRequestDTO;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.dto.request.DetallePedidoRequest;
import com.koroFoods.orderService.dto.response.DetallePedidoResponse;
import com.koroFoods.orderService.enums.EstadoDetallePedido;
import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.feign.MesaFeignClient;
import com.koroFoods.orderService.feign.PlatoFeign;
import com.koroFoods.orderService.feign.PlatoFeignClient;
import com.koroFoods.orderService.feign.UsuarioFeignClient;
import com.koroFoods.orderService.model.DetallePedido;
import com.koroFoods.orderService.model.Pedido;
import com.koroFoods.orderService.repository.IDetallePedidoRepository;
import com.koroFoods.orderService.repository.IPedidoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final IPedidoRepository pedidoRepository;
    private final IDetallePedidoRepository detallePedidoRepository;
    private final MesaFeignClient mesaFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;
    private final PlatoFeignClient platoFeignClient;

    public ResultadoResponse<List<PedidoResumenDto>> listarPedidos(EstadoPedido estado) {
        List<Pedido> pedidos = pedidoRepository.findByEstadoOpcional(estado);

        List<PedidoResumenDto> dtos = pedidos.stream().map(pedido -> {
            PedidoResumenDto dto = new PedidoResumenDto();
            dto.setIdPedido(pedido.getIdPedido());
            dto.setIdMesa(pedido.getIdMesa());
            dto.setFechaHora(pedido.getFechaHora());
            dto.setEstado(pedido.getEstado());
            dto.setTotal(pedido.getTotal());
            return dto;
        }).toList();

        return ResultadoResponse.success("Listado encontrado", dtos);
    }

    public ResultadoResponse<PedidoResumenDto> obtenerPedidoPorReserva(Integer idReserva) {
        Pedido pedido = pedidoRepository.findByIdReserva(idReserva);

        if (pedido == null) {
            return ResultadoResponse.success("No existe pedido para esta reserva", null);
        }

        PedidoResumenDto dto = new PedidoResumenDto();
        dto.setIdPedido(pedido.getIdPedido());
        dto.setFechaHora(pedido.getFechaHora());
        dto.setTotal(pedido.getTotal());
        dto.setIdMesa(pedido.getIdMesa());
        dto.setEstado(pedido.getEstado());

        return ResultadoResponse.success("Pedido encontrado", dto);
    }

    @Transactional
    public ResultadoResponse<Pedido> crearPedido(PedidoRequestDTO dto) {
        var mesaResp = mesaFeignClient.getTableById(dto.getIdMesa());
        if (!mesaResp.isValor() || mesaResp.getData() == null) {
            throw new RuntimeException("La mesa no existe");
        }

        var usuarioResp = usuarioFeignClient.getUsuarioById(dto.getIdUsuario());
        if (!usuarioResp.isValor() || usuarioResp.getData() == null) {
            throw new RuntimeException("El usuario no existe");
        }

        Pedido pedido = new Pedido();
        pedido.setIdMesa(dto.getIdMesa());
        pedido.setIdUsuario(dto.getIdUsuario());
        pedido.setIdReserva(dto.getIdReserva());
        pedido.setFechaHora(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.EP);
        pedido.setSubtotal(BigDecimal.ZERO);
        pedido.setTotal(BigDecimal.ZERO);

        pedido = pedidoRepository.save(pedido);

        BigDecimal subtotalPedido = BigDecimal.ZERO;

        for (DetallePedidoRequestDTO d : dto.getDetalles()) {
            var platoResp = platoFeignClient.getDishById(d.getIdPlato());
            if (!platoResp.isValor() || platoResp.getData() == null) {
                throw new RuntimeException("El plato con ID " + d.getIdPlato() + " no existe.");
            }

            BigDecimal precioUnit = platoResp.getData().getPrecio();
            BigDecimal subtotal = precioUnit.multiply(BigDecimal.valueOf(d.getCantidad()));

            DetallePedido detalle = new DetallePedido();
            detalle.setIdPedido(pedido.getIdPedido());
            detalle.setIdPlato(d.getIdPlato());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(precioUnit);
            detalle.setSubtotal(subtotal);
            detalle.setEstado(EstadoDetallePedido.PED);

            detallePedidoRepository.save(detalle);
            platoFeignClient.substractStockOrder(d.getIdPlato(), d.getCantidad());

            subtotalPedido = subtotalPedido.add(subtotal);
        }

        pedido.setSubtotal(subtotalPedido);
        pedido.setTotal(subtotalPedido);
        pedidoRepository.save(pedido);

        return ResultadoResponse.success("El pedido fue generado satisfactoriamente.", pedido);
    }


    @Transactional
    public ResultadoResponse<DetallePedido> registrarPlato(DetallePedidoRequest request) {

        //Validamos
        validarRequest(request);

        //Obtenemos pedido y plato
        Pedido actualizarPedido = obtenerPedido(request.getIdPedido());
        var platoData = obtnerPlatoValidar(request.getIdPlato(), request.getCantidad());

        //Registramos el plato a un pedido existente
        DetallePedido registrar = crearDetallePedido(request, platoData);
        detallePedidoRepository.save(registrar);

        //Actualizamos el stock del plato los subtotales etc
        actualizarStockPlato(request.getIdPlato(), request.getCantidad());
        actualizarTotalesPedido(actualizarPedido, registrar.getSubtotal());


        return ResultadoResponse.success("Se agrego un nuevo plato a tu orden  " + platoData.getNombre(), registrar);
    }


    private void validarRequest(DetallePedidoRequest request) {
        if (request == null) {
            throw new RuntimeException("Request Null");
        }
    }

    private Pedido obtenerPedido(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error al obtener el pedido: " + id));
    }

    private PlatoFeign obtnerPlatoValidar(Integer idPlato, Integer cantidad) {
        var plato = platoFeignClient.getDishById(idPlato);

        if (plato.getData() == null) {
            throw new RuntimeException("Error al obtner el plato" + idPlato);
        }

        var platoData = plato.getData();
        validarStockDisponible(platoData, cantidad);

        return platoData;
    }

    private void validarStockDisponible(PlatoFeign plato, Integer cantidad) {
        if (cantidad > plato.getStock()) {
            throw new RuntimeException(
                    String.format("Cantidad seleccionada (%d) supera al stock disponible (%d)",
                            cantidad, plato.getStock())
            );
        }
    }

    private DetallePedido crearDetallePedido(DetallePedidoRequest request, PlatoFeign plato) {
        BigDecimal subtotal = calcularSubtotal(plato.getPrecio(), request.getCantidad());

        DetallePedido detalle = new DetallePedido();
        detalle.setIdPedido(request.getIdPedido());
        detalle.setIdPlato(request.getIdPlato());
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(plato.getPrecio());
        detalle.setSubtotal(subtotal);
        detalle.setEstado(EstadoDetallePedido.PED);

        return detalle;
    }

    private BigDecimal calcularSubtotal(BigDecimal precioUnitario, Integer cantidad) {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    private void actualizarStockPlato(Integer idPlato, Integer cantidad) {
        platoFeignClient.substractStockOrder(idPlato, cantidad);
    }

    private void actualizarTotalesPedido(Pedido pedido, BigDecimal nuevoSubtotal) {
        pedido.setSubtotal(pedido.getSubtotal().add(nuevoSubtotal));
        pedido.setTotal(pedido.getTotal().add(nuevoSubtotal));
        pedidoRepository.save(pedido);
    }


    public ResultadoResponse<List<DetallePedidoResponse>> obtenerDetallePorPedido(Integer pedidoId) {

        if (pedidoId == null ||pedidoId <= 0) {
            return ResultadoResponse.error("Id Invalido", null);
        }

        List<DetallePedido> detalles =
                detallePedidoRepository.findByIdPedidoDescEstado(pedidoId);
        List<DetallePedidoResponse> response =
                detalles
                        .stream()
                        .map(this::mapToResponse)
                        .toList();
        return ResultadoResponse.success("Lista Obtenida! " ,response);

    }

    private DetallePedidoResponse mapToResponse(DetallePedido dp){

        var platoObtenido = platoFeignClient.getDishById(dp.getIdPlato());
        var platoData =  platoObtenido.getData();

        DetallePedidoResponse rs = new DetallePedidoResponse();

        rs.setIdDetalle(dp.getIdDetalle());
        rs.setIdPedido(dp.getIdPedido());
        rs.setIdPlato(dp.getIdPlato());

        rs.setImagen(platoData.getImagen());
        rs.setNombre(platoData.getNombre());

        rs.setCantidad(dp.getCantidad());
        rs.setEstado(dp.getEstado());
        rs.setPrecioUnitario(dp.getPrecioUnitario());
        rs.setSubtotal(dp.getSubtotal());
        return rs;
    }

}
