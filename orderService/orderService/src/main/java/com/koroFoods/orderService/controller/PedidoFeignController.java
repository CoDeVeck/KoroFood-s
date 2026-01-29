package com.koroFoods.orderService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.orderService.dto.PedidoResumenDto;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.service.PedidoService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/pedido/feign")
public class PedidoFeignController {
	
	private final PedidoService pedidoService;

	@GetMapping("/reserva/{idReserva}")
	public ResponseEntity<ResultadoResponse<PedidoResumenDto>> getPedidoByReservaId(@PathVariable Integer idReserva) {
		ResultadoResponse<PedidoResumenDto> resultado = pedidoService.obtenerPedidoPorReserva(idReserva);
		if (resultado.isValor()) {
			return ResponseEntity.ok(resultado);
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
}
