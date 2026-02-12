package com.koroFoods.reservationService.feign;

import com.koroFoods.reservationService.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "userService", configuration = FeignConfig.class)
public interface UsuarioFeignClient {
	@GetMapping("/user/feign/{id}")
    ResultadoResponse<UsuarioFeign> getUsuarioById(@PathVariable Integer id);
}
