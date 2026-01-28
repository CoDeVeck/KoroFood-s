package com.koroFoods.qualificationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.qualificationService.dto.ResultadoResponse;

@FeignClient(name = "plato-service", url = "http://localhost:8087/menu")
public interface PlatoFeignClient {
	@GetMapping("/{id}")
    ResultadoResponse<PlatoFeign> getDishById(@PathVariable Integer id);
}
