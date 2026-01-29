package com.koroFoods.reservationService.service;

import com.koroFoods.reservationService.dto.ReservaDtoFeing;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.feign.EventoFeignClient;
import com.koroFoods.reservationService.feign.PedidoFeignClient;
import com.koroFoods.reservationService.feign.UsuarioFeignClient;
import com.koroFoods.reservationService.model.Reserva;
import com.koroFoods.reservationService.repository.IReservaRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private final IReservaRepository reservaRepository;
	private final UsuarioFeignClient usuarioFeignClient;
	private final PedidoFeignClient pedidoFeignClient;

	private final EventoFeignClient eventoFeignClient;

	public ResultadoResponse<ReservaDtoFeing> getReservationByID(String codigo) {
		Optional<Reserva> optionalReserva = reservaRepository.findReservaAsistidaById(codigo);

		if (optionalReserva.isEmpty()) {
			return ResultadoResponse.error("Reserva no encontrada, intente con otro código por favor");
		}

		Reserva reserva = optionalReserva.get();

		try {
			var pedidoResponse = pedidoFeignClient.getPedidoByReservaId(reserva.getIdReserva());

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

	/*
	 * Reserva Normal----- Horario local (12:00 – 23:00) generarSlots
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

		// 1️⃣ Validar contra EVENTO_MESA SOLO si es evento
		if (esEvento) {
			try {
			    ResultadoResponse<Boolean> response =
			            eventoFeignClient.validarHorariosParaReservaConEvento(
			                    idMesa, idEvento, desde, hasta
			            );

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

	/*
	 * public ResultadoResponse<Reserva> registrarReserva(Reserva req) { Reserva reg
	 * = new Reserva();
	 * 
	 * var usuario = usuarioFeignClient.getUsuarioById(req.getIdUsuario());
	 * 
	 * if (!usuario.isValor() || usuario.getData() == null) { return
	 * ResultadoResponse.error( "El usuario con ID " + req.getIdUsuario() +
	 * " no existe" ); }
	 * 
	 * 
	 * 
	 * reg.setIdReserva(null); reg.setIdUsuario(req.getIdUsuario());
	 * reg.setIdMesa(req.getIdMesa());
	 * 
	 * if (reg.getIdEvento().equals(null)) { reg.setIdEvento(null); } else {
	 * reg.setIdEvento(req.getIdEvento()); }
	 * 
	 * }
	 */
}
