package com.koroFoods.reservationService.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.service.ReservaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {
	private final ReservaService reservaService;

	// cuando el usuario ya escogio una hora
	@GetMapping("/desocupado")
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

}
