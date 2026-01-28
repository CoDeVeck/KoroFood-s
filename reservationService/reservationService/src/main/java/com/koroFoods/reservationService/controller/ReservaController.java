package com.koroFoods.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.reservationService.dto.ReservaDtoFeing;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.service.ReservaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {
	private final ReservaService reservaService;

	@GetMapping("/{codigo}")
	public ResponseEntity<ResultadoResponse<ReservaDtoFeing>> getreservationhById(@PathVariable String codigo) {
		ResultadoResponse<ReservaDtoFeing> resultado = reservaService.getReservationByID(codigo);
		if (resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
		}
	}
}
