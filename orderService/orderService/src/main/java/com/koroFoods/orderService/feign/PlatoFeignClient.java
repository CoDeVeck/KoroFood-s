package com.koroFoods.orderService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.koroFoods.orderService.dto.ResultadoResponse;


@FeignClient(name = "menuService")
public interface PlatoFeignClient {
	@GetMapping("/menu/feign/{id}")
    ResultadoResponse<PlatoFeign> getDishById(@PathVariable Integer id);
	
	@PutMapping("/menu/feign/substract-stock/{idPlato}/{cantidadVendida}")
    ResultadoResponse<PlatoFeign> substractStockOrder(
			@PathVariable Integer idPlato, @PathVariable Integer cantidadVendida);
}
