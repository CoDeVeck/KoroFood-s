package com.koroFoods.orderService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.orderService.dto.PedidoRequestDTO;
import com.koroFoods.orderService.dto.PedidoResumenDto;
import com.koroFoods.orderService.dto.ResultadoResponse;
import com.koroFoods.orderService.enums.EstadoPedido;
import com.koroFoods.orderService.model.Pedido;
import com.koroFoods.orderService.service.PedidoService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pedido")
public class PedidoController {
	private final PedidoService pedidoService;

	@GetMapping
	public ResponseEntity<ResultadoResponse<List<PedidoResumenDto>>> list(
	        @RequestParam(required = false) EstadoPedido estado) {
	    ResultadoResponse<List<PedidoResumenDto>> resultado = pedidoService.listarPedidos(estado);

	    if(resultado.isValor()) {
	        return ResponseEntity.ok(resultado);
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}

	
	@PostMapping
	public ResponseEntity<ResultadoResponse<Pedido>> crearPedido(@RequestBody PedidoRequestDTO dto) {

	    System.out.println("📥 Llegó al controlador. DTO recibido: " + dto);

	    ResultadoResponse<Pedido> resultado = pedidoService.crearPedido(dto);

	    System.out.println("📤 Resultado del service: " + resultado);

	    if (resultado.isValor()) {
	        return ResponseEntity.status(HttpStatus.CREATED).body(resultado); 
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}


}
