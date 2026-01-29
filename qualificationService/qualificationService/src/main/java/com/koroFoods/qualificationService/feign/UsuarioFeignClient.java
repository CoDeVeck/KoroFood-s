package com.koroFoods.qualificationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.qualificationService.dto.ResultadoResponse;

@FeignClient(name = "user-service", url = "http://localhost:8081/user/feign")
public interface UsuarioFeignClient {
	@GetMapping("/{id}")
    ResultadoResponse<UsuarioFeign> getUsuarioById(@PathVariable Integer id);
}
