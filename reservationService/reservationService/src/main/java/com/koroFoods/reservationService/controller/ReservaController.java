package com.koroFoods.reservationService.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.reservationService.dto.RecepcionistaCountsDTO;
import com.koroFoods.reservationService.dto.ReservaAsistidaDTO;
import com.koroFoods.reservationService.dto.ReservaRequest;
import com.koroFoods.reservationService.dto.ReservaResponse;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.service.ReservaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {
	private final ReservaService reservaService;

	// cuando el usuario ya escogio una hora
	@GetMapping("/ocupada")
	public ResultadoResponse<Boolean> validarReserva(@RequestParam Integer mesaId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
			@RequestParam(defaultValue = "false") boolean esEvento) {

		boolean ocupada = reservaService.mesaOcupadaPorReserva(mesaId, fechaHora, esEvento);

		return ResultadoResponse.success("Validación realizada", ocupada);
	}

	@GetMapping("/slots-disponibles")
	public ResultadoResponse<List<LocalDateTime>> obtenerSlotsDisponibles(@RequestParam Integer mesaId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
			@RequestParam(required = false) Integer eventoId) {

		List<LocalDateTime> slots = reservaService.obtenerSlotsDisponibles(mesaId, desde, hasta, eventoId);

		return ResultadoResponse.success("Slots disponibles obtenidos", slots);
	}

	@PostMapping("/registro")
	public ResultadoResponse<Integer> registrarReserva(@RequestBody ReservaRequest request) {
		return reservaService.registrarReserva(request);
	}

	@GetMapping("/mis-reservas/{idUsuario}")
	public ResultadoResponse<List<ReservaResponse>> listarReservasDelCliente(@PathVariable Integer idUsuario) {
		return reservaService.listarReservasPorCliente(idUsuario);
	}

	@PatchMapping("/cancelar/{idReserva}")
	public ResultadoResponse<Integer> cancelarReserva(@PathVariable Integer idReserva) {
	    return reservaService.cancelarReservaPagada(idReserva);
	}
	
	@GetMapping("/dashboard/recepcionista/counts")
	public ResponseEntity<RecepcionistaCountsDTO> obtenerCounts() {
	    return ResponseEntity.ok(reservaService.obtenerCounts());
	}
	
	@GetMapping("/dashboard/recepcionista/asistidas/hoy")
	public ResponseEntity<ResultadoResponse<List<ReservaAsistidaDTO>>> listarReservasAsistidasPorDia() {
	    return ResponseEntity.ok(reservaService.listarReservasAsistidasPorDia());
	}
}
