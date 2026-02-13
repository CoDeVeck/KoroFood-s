package com.koroFoods.reservationService.service;

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

		LocalDateTime inicio = request.getFechaHora();
		LocalDateTime fin = inicio.plusHours(esEvento ? 3 : 2);

		if (esEvento) {
			ResultadoResponse<Boolean> response = eventoFeignClient
					.validarHorariosParaReservaConEvento(request.getIdMesa(), request.getIdEvento(), inicio, fin);

			if (!response.isValor() || !Boolean.TRUE.equals(response.getData())) {
				return ResultadoResponse.error("La mesa no está asignada al evento seleccionado");
			}

		}

		boolean ocupada = reservaRepository.existeSolapamientoReserva(request.getIdMesa(), inicio, fin);

		if (ocupada) {
			return ResultadoResponse.error("La mesa ya se encuentra reservada en el horario seleccionado");
		}

		Reserva reserva = new Reserva();
		reserva.setIdUsuario(request.getIdUsuario());
		reserva.setIdMesa(request.getIdMesa());
		reserva.setIdEvento(request.getIdEvento());
		reserva.setTipoReserva(reserva.getIdEvento() != null ? TipoReserva.ESPECIAL : TipoReserva.SIMPLE);
		reserva.setFechaHora(inicio);
		reserva.setEstado(EstadoReserva.PENDIENTE);
		reserva.setFechaRegistro(LocalDateTime.now());
		reserva.setObservaciones(request.getObservaciones());
		reserva.setVerificado(false);

		reservaRepository.save(reserva);

		return ResultadoResponse.success("Reserva registrada correctamente. Pendiente de pago.",
				reserva.getIdReserva());
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

	/*
	 * Reserva Simple----- Horario local (12:00 – 23:00) generarSlots
	 * filtrarSlotsDisponibles frontend muestra horas libres usuario elige una
	 * mesaOcupadaPorReserva (check final)
	 *
	 *
	 * Reserva Especial---- Horario evento (19:00 – 22:00) generarSlots
	 * filtrarSlotsDisponibles usuario elige mesaOcupadaPorReserva
	 *
	 */

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
