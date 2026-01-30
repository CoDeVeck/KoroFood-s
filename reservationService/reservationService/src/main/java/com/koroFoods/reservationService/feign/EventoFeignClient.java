package com.koroFoods.reservationService.feign;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "evento-service", url = "http://localhost:8088/evento/feign")
public interface EventoFeignClient {

	@GetMapping("/validar/{id}")
	ResultadoResponse<EventoFeign> obtenerEvento(@PathVariable Integer id);

	@GetMapping("/ocupaciones")
	ResultadoResponse<Boolean> validarHorariosParaReservaConEvento(@RequestParam Integer mesaId,
			@RequestParam Integer eventoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta);

}
