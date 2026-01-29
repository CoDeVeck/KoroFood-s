package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "evento-service", url = "http://localhost:8088/evento/feign")
public interface EventoFeignClient {

	@GetMapping("/validar/{id}")
	ResultadoResponse<EventoFeign> obtenerEvento(@PathVariable Integer id);
	
	
	
}
