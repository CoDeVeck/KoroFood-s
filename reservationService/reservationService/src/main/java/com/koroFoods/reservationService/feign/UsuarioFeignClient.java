package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UsuarioFeignClient {

	@GetMapping("/user/feign/{id}")
	ResultadoResponse<UsuarioFeign> getUsuarioById(@PathVariable Integer id);

}
