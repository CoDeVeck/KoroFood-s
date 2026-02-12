package com.koroFoods.orderService.feign;

import com.koroFoods.orderService.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.orderService.dto.ResultadoResponse;


@FeignClient(name = "userService", configuration = FeignConfig.class)
public interface UsuarioFeignClient {
	@GetMapping("/user/feign/{id}")
    ResultadoResponse<UsuarioFeign> getUsuarioById(@PathVariable Integer id);
}
