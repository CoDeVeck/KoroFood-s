package com.koroFoods.orderService.feign;

import com.koroFoods.orderService.config.FeignConfig;
import com.koroFoods.orderService.dto.ResultadoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reservationService", configuration = FeignConfig.class)
public interface ReservaFeignClient {
    @GetMapping("/reserva/feign/reserva/{idReserva}")
    ResultadoResponse<UsuarioFeign>obtenerUsuarioPorReserva(@PathVariable Integer idReserva);
}
