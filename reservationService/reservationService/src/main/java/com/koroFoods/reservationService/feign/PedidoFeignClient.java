package com.koroFoods.reservationService.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.koroFoods.reservationService.dto.ResultadoResponse;

@FeignClient(name = "pedido-service", url = "http://localhost:8086/pedido/feign")
public interface PedidoFeignClient {
    
    @GetMapping("/reserva/{idReserva}")
    ResultadoResponse<PedidoFeign> getPedidoByReservaId(@PathVariable("idReserva") Integer idReserva);
}