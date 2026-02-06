package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "tableService")
public interface MesaFeignClient {

	@GetMapping("/mesa/feign/{/id}")
	ResultadoResponse<MesaFeign> obtenerMesaPorId(@PathVariable Integer id);
}
