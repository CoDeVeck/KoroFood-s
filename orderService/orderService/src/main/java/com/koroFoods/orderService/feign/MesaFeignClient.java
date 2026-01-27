package com.koroFoods.orderService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.orderService.dto.ResultadoResponse;

@FeignClient(name = "mesa-service", url = "http://localhost:8082/mesa")
public interface MesaFeignClient {
	@GetMapping("/{id}")
    ResultadoResponse<MesaFeign> getTableById(@PathVariable int id);
}
