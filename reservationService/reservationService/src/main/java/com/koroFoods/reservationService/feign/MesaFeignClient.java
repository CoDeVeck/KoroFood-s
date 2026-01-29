package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "mesas", url = "http://localhost:8082/mesa/feign")
public interface MesaFeignClient {

	@GetMapping("{/id}")
	ResultadoResponse<MesaFeign> obtenerMesaPorId(@PathVariable Integer id);
}
