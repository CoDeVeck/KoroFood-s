package com.koroFoods.qualificationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.qualificationService.dto.ResultadoResponse;

@FeignClient(name = "event-service", url = "http://localhost:8088/evento/feign")
public interface EventoFeignClient {
	@GetMapping("/{id}")
    ResultadoResponse<EventoFeign> getEventById(@PathVariable Integer id);
}
