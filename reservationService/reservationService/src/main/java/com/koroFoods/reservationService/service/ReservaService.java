package com.koroFoods.reservationService.service;

import com.koroFoods.reservationService.dto.RecepcionistaCountsDTO;
import com.koroFoods.reservationService.dto.ReservaAsistidaDTO;
import com.koroFoods.reservationService.dto.ReservaDtoFeing;
import com.koroFoods.reservationService.dto.ReservaRequest;
import com.koroFoods.reservationService.dto.ReservaResponse;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.enums.EstadoReserva;
import com.koroFoods.reservationService.enums.TipoReserva;
import com.koroFoods.reservationService.feign.EventoFeign;
import com.koroFoods.reservationService.feign.EventoFeignClient;
import com.koroFoods.reservationService.feign.MesaFeign;
import com.koroFoods.reservationService.feign.MesaFeignClient;
import com.koroFoods.reservationService.feign.PedidoFeignClient;
import com.koroFoods.reservationService.feign.UsuarioFeign;
import com.koroFoods.reservationService.feign.UsuarioFeignClient;
import com.koroFoods.reservationService.model.Reserva;
import com.koroFoods.reservationService.repository.IReservaRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private final IReservaRepository reservaRepository;
	private final UsuarioFeignClient usuarioFeignClient;
	private final PedidoFeignClient pedidoFeignClient;

	private final EventoFeignClient eventoFeignClient;
	private final MesaFeignClient mesaFeignClient;

	public ResultadoResponse<Integer> registrarReserva(ReservaRequest request) {

	    boolean esEvento = request.getIdEvento() != null;

	    LocalDateTime inicio;
	    LocalDateTime fin;

	    try {
	        inicio = LocalDateTime.parse(request.getFechaHora());
	    } catch (Exception e) {
	        return ResultadoResponse.error("Formato de fecha invalido");
	    }

	    if (esEvento) {
	        ResultadoResponse<EventoFeign> eventoResponse = eventoFeignClient
	                .obtenerEvento(request.getIdEvento());

	        if (eventoResponse == null || eventoResponse.getData() == null) {
	            return ResultadoResponse.error("No se pudo obtener la información del evento");
	        }

	        EventoFeign evento = eventoResponse.getData();
	        inicio = evento.getFechaInicio();
	        fin = evento.getFechaFin();

	        ResultadoResponse<Boolean> validacion = eventoFeignClient
	                .validarHorariosParaReservaConEvento(
	                        request.getIdMesa(),
	                        request.getIdEvento(),
	                        inicio,
	                        fin
	                );

	        if (validacion == null || !validacion.isValor() || !Boolean.TRUE.equals(validacion.getData())) {
	            return ResultadoResponse.error("La mesa no está asignada al evento seleccionado");
	        }

	    } else {
	        fin = inicio.plusHours(2);
	    }

	    boolean ocupada = reservaRepository.existeSolapamientoReserva(request.getIdMesa(), inicio, fin);

	    if (ocupada) {
	        return ResultadoResponse.error("La mesa ya se encuentra reservada en el horario seleccionado");
	    }

	    Reserva reserva = new Reserva();
	    reserva.setIdUsuario(request.getIdUsuario());
	    reserva.setIdMesa(request.getIdMesa());
	    reserva.setIdEvento(request.getIdEvento());
	    reserva.setTipoReserva(esEvento ? TipoReserva.ESPECIAL : TipoReserva.SIMPLE);
	    reserva.setFechaHora(inicio);
	    reserva.setEstado(EstadoReserva.PAGADA);
	    reserva.setFechaRegistro(LocalDateTime.now());
	    reserva.setObservaciones(request.getObservaciones());
	    reserva.setVerificado(false);

	    reservaRepository.save(reserva);

	    return ResultadoResponse.success("Reserva registrada correctamente.", reserva.getIdReserva());
	}
	
	public ResultadoResponse<List<Integer>> obtenerMesasOcupadas(
	        List<Integer> idsMesas, LocalDateTime inicio, LocalDateTime fin) {

	    List<Integer> ocupadas = reservaRepository.findMesasOcupadasEnRango(idsMesas, inicio, fin);
	    return ResultadoResponse.success("Mesas ocupadas en el rango indicado", ocupadas);
	}
	
	
	public ResultadoResponse<ReservaDtoFeing> getReservationByID(String codigo) {
		Optional<Reserva> optionalReserva = reservaRepository.findReservaAsistidaById(codigo);

		if (optionalReserva.isEmpty()) {
			return ResultadoResponse.error("Reserva no encontrada, intente con otro código por favor");
		}

		Reserva reserva = optionalReserva.get();

		try {
			var pedidoResponse = pedidoFeignClient.getPedidoByIdReserva(reserva.getIdReserva());

			if (pedidoResponse.isValor() && pedidoResponse.getData() != null) {
				return ResultadoResponse.error("Esta reserva ya tiene un pedido asociado.");
			}
		} catch (FeignException.NotFound e) {

		} catch (Exception e) {
			return ResultadoResponse.error("Error al validar el pedido de la reserva: " + e.getMessage());
		}

		var usuario = usuarioFeignClient.getUsuarioById(reserva.getIdUsuario());

		if (!usuario.isValor() || usuario.getData() == null) {
			return ResultadoResponse.error("El usuario con ID " + reserva.getIdUsuario() + " no existe");
		}

		ReservaDtoFeing dto = new ReservaDtoFeing();
		dto.setIdReserva(reserva.getIdReserva());
		dto.setEstado(reserva.getEstado().toString());
		dto.setIdUsuario(reserva.getIdUsuario());
		dto.setMesa(reserva.getIdMesa());
		dto.setNombreCompletoUsuario(usuario.getData().getNombres() + " " + usuario.getData().getApePaterno() + " "
				+ usuario.getData().getApeMaterno());

		return ResultadoResponse.success("Reserva encontrada", dto);
	}

	public ResultadoResponse<List<ReservaResponse>> listarReservasPorCliente(Integer idUsuario) {
		List<Reserva> reservas = reservaRepository.findByIdUsuario(idUsuario);

		if (reservas.isEmpty()) {
			return ResultadoResponse.error("El usuario no tiene reservas registradas");
		}
		List<ReservaResponse> response = reservas.stream().map(reserva -> {
			ReservaResponse dto = new ReservaResponse();

			dto.setIdReserva(reserva.getIdReserva());
			dto.setTipoReserva(reserva.getTipoReserva());
			dto.setFechaHora(reserva.getFechaHora());
			dto.setEstado(reserva.getEstado());
			dto.setObservaciones(reserva.getObservaciones());

			ResultadoResponse<UsuarioFeign> usuarioResult = usuarioFeignClient.getUsuarioById(reserva.getIdUsuario());

			if (usuarioResult.isValor() && usuarioResult.getData() != null) {
				UsuarioFeign usuario = usuarioResult.getData();
				dto.setNombreCli(usuario.getNombres());
				dto.setApellidoPa(usuario.getApePaterno());
				dto.setApellidoMa(usuario.getApeMaterno());
			}

			ResultadoResponse<MesaFeign> mesaResult = mesaFeignClient.obtenerMesaPorId(reserva.getIdMesa());

			if (mesaResult.isValor() && mesaResult.getData() != null) {
				MesaFeign mesa = mesaResult.getData();
				dto.setNumMesa(mesa.getNumeroMesa());
				dto.setCapacidad(mesa.getCapacidad());
				dto.setZona(mesa.getTipo());
			}

			if (reserva.getIdEvento() != null) {
				ResultadoResponse<EventoFeign> eventoResult = eventoFeignClient.obtenerEvento(reserva.getIdEvento());

				if (eventoResult.isValor() && eventoResult.getData() != null) {
					EventoFeign evento = eventoResult.getData();
					dto.setIdEvento(evento.getIdEvento());
					dto.setNombreEvento(evento.getNombre());
					dto.setFechaInicio(evento.getFechaInicio());
					dto.setFechaFin(evento.getFechaFin());
				}
			}

			return dto;
		}).collect(Collectors.toList());

		return ResultadoResponse.success("Reservas obtenidas correctamente", response);
	}
	
	public ResultadoResponse<Integer> cancelarReservaPagada(Integer idReserva) {
	    Optional<Reserva> optional = reservaRepository.findReservaPagadaById(idReserva);

	    if (optional.isEmpty()) {
	        return ResultadoResponse.error("No se encontró una reserva PAGADA con el ID: " + idReserva);
	    }

	    Reserva reserva = optional.get();
	    reserva.setEstado(EstadoReserva.CANCELADA);
	    reservaRepository.save(reserva);

	    return ResultadoResponse.success("Reserva cancelada correctamente", reserva.getIdReserva());
	}

	public RecepcionistaCountsDTO obtenerCounts() {
		LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();         
		LocalDateTime finHoy    = inicioHoy.plusDays(1);                   
		LocalDateTime inicioTomorrow = inicioHoy.plusDays(1);
		LocalDateTime finTomorrow    = inicioHoy.plusDays(2);

		long reservasHoy      = reservaRepository.countReservasEntreFechas(inicioHoy, finHoy);
		long reservasAsistidas = reservaRepository.countReservasAsistidas(inicioHoy, finHoy);
		long reservasTomorrow   = reservaRepository.countReservasEntreFechas(inicioTomorrow, finTomorrow);
	    long reservasPendientes = reservasHoy - reservasAsistidas;


	    return new RecepcionistaCountsDTO(reservasHoy, reservasAsistidas, reservasPendientes, reservasTomorrow);
	}
	
	public ResultadoResponse<List<ReservaAsistidaDTO>> listarReservasAsistidasPorDia() {
	    LocalDateTime inicio = LocalDate.now().atStartOfDay();
	    LocalDateTime fin    = inicio.plusDays(1);

	    List<Reserva> reservas = reservaRepository.findReservasAsistidasPorFecha(inicio, fin);

	    List<ReservaAsistidaDTO> resultado = reservas.stream()
	            .map(this::mapearAReservaAsistidaDTO)
	            .collect(Collectors.toList());

	    return ResultadoResponse.success("Reservas asistidas del día", resultado);
	}

	private ReservaAsistidaDTO mapearAReservaAsistidaDTO(Reserva r) {
	    ReservaAsistidaDTO dto = new ReservaAsistidaDTO();

	    dto.setIdReserva(r.getIdReserva());
	    dto.setFechaReserva(r.getFechaHora());
	    dto.setObservaciones(r.getObservaciones());

	    // Usuario
	    try {
	        ResultadoResponse<UsuarioFeign> usuarioResp = usuarioFeignClient.getUsuarioById(r.getIdUsuario());
	        if (usuarioResp != null && usuarioResp.getData() != null) {
	            dto.setNombreCliente(usuarioResp.getData().getNombreCompleto());
	        }
	    } catch (Exception e) {
	        dto.setNombreCliente("Sin información");
	    }

	    // Mesa
	    if (r.getIdMesa() != null) {
	        try {
	            ResultadoResponse<MesaFeign> mesaResp = mesaFeignClient.obtenerMesaPorId(r.getIdMesa());
	            if (mesaResp != null && mesaResp.getData() != null) {
	                dto.setMesa(mesaResp.getData().getNumeroMesa());
	                dto.setZona(mesaResp.getData().getTipo());
	            }
	        } catch (Exception e) {
	            dto.setMesa(r.getIdMesa());
	            dto.setZona("-");
	        }
	    }

	    // Evento (opcional, puede ser reserva sin evento)
	    if (r.getIdEvento() != null) {
	        try {
	            ResultadoResponse<EventoFeign> eventoResp = eventoFeignClient.obtenerEvento(r.getIdEvento());
	            if (eventoResp != null && eventoResp.getData() != null) {
	                dto.setEvento(eventoResp.getData().getNombre());
	                dto.setTematica(eventoResp.getData().getTematica());
	            }
	        } catch (Exception e) {
	            dto.setEvento("-");
	            dto.setTematica("-");
	        }
	    }

	    return dto;
	}
	
	/*
	 * Reserva Simple----- Horario local (12:00 – 23:00) generarSlots
	 * filtrarSlotsDisponibles frontend muestra horas libres usuario elige una
	 * mesaOcupadaPorReserva (check final)
	 *
	 */

    public ResultadoResponse<List<Reserva>> obtenerReservaPorIdCliente(Integer idCliente){
        validarId(idCliente);

        List<Reserva> lista = reservaRepository.findByIdUsuario(idCliente);

        if (lista.isEmpty()){
            return ResultadoResponse.error("Error al obtener lista: ", null);
        }

        return ResultadoResponse.success("Se obtuvo " + lista.size() + " reservas: ", lista);
    }



	private int obtenerDuracionHoras(boolean esEvento) {
		return esEvento ? 3 : 2;
	}

	// 1
	private List<LocalDateTime> generarSlots(LocalDateTime desde, LocalDateTime hasta, int intervaloMinutos) {

		List<LocalDateTime> slots = new ArrayList<>();
		LocalDateTime actual = desde;

		while (!actual.isAfter(hasta)) {
			slots.add(actual);
			actual = actual.plusMinutes(intervaloMinutos);
		}

		return slots;
	}

	// 2
	private List<LocalDateTime> filtrarSlotsDisponibles(Integer idMesa, List<LocalDateTime> slots, boolean esEvento) {

		int duracionHoras = obtenerDuracionHoras(esEvento);

		return slots.stream().filter(slot -> {
			LocalDateTime fin = slot.plusHours(duracionHoras);
			return !reservaRepository.existeSolapamientoReserva(idMesa, slot, fin);
		}).toList();
	}

	// 3 - Controller
	public List<LocalDateTime> obtenerSlotsDisponibles(Integer idMesa, LocalDateTime desde, LocalDateTime hasta,
			Integer idEvento) {

		boolean esEvento = idEvento != null;

		// 1️ Validar contra EVENTO_MESA SOLO si es evento
		if (esEvento) {
			try {
				ResultadoResponse<Boolean> response = eventoFeignClient.validarHorariosParaReservaConEvento(idMesa,
						idEvento, desde, hasta);

				if (!response.isValor() || !Boolean.TRUE.equals(response.getData())) {
					return List.of();
				}
			} catch (FeignException e) {
				return List.of(); // evento no accesible → no habilitar slots
			}

		}

		List<LocalDateTime> slots = generarSlots(desde, hasta, 30);

		return filtrarSlotsDisponibles(idMesa, slots, esEvento);
	}

	// Validar cuando el cliente haya eligido una hora/fecha
	public boolean mesaOcupadaPorReserva(Integer idMesa, LocalDateTime fechaInicio, boolean esEvento) {

		int duracion = obtenerDuracionHoras(esEvento);

		return reservaRepository.existeSolapamientoReserva(idMesa, fechaInicio, fechaInicio.plusHours(duracion));
	}

	public ResultadoResponse<UsuarioFeign> obtenerUsuarioPorReserva(Integer idReserva) {

		validarId(idReserva);

		Reserva reservaObtenida = obtenerReserva(idReserva);
		ResultadoResponse<UsuarioFeign> cliente = usuarioFeignClient.getUsuarioById(reservaObtenida.getIdUsuario());
		var clienteData = cliente.getData();

		return ResultadoResponse.success("Se obtuvo al cliente ", clienteData);
	}

	private Reserva obtenerReserva(Integer idReserva) {
		validarId(idReserva);
		return reservaRepository.findById(idReserva)
				.orElseThrow(() -> new RuntimeException("Error al obtener la reserva" + idReserva));
	}

	private void validarId(Integer request) {
		if (request == null || request <= 0) {
			throw new IllegalArgumentException("ID invalido");
		}
	}

}
