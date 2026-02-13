package com.koroFoods.orderService.controller;

import java.util.List;

import com.koroFoods.orderService.dto.request.DetallePedidoRequest;
import com.koroFoods.orderService.dto.response.DetallePedidoResponse;
import com.koroFoods.orderService.dto.response.DetallePedidoUsuarioResponse;
import com.koroFoods.orderService.model.DetallePedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        if (resultado.isValor()) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }


    @PostMapping
    public ResponseEntity<ResultadoResponse<Pedido>> crearPedido(@RequestBody PedidoRequestDTO dto) {
        ResultadoResponse<Pedido> resultado = pedidoService.crearPedido(dto);
        if (resultado.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }

    @PostMapping("/newPlato")
    public ResponseEntity<ResultadoResponse<DetallePedido>> agregarPlatoOrden(@RequestBody DetallePedidoRequest request) {
        ResultadoResponse<DetallePedido> resultado = pedidoService.registrarPlato(request);
        if (resultado.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }

    @GetMapping("/list/{pedidoId}")
    public ResponseEntity<ResultadoResponse<List<DetallePedidoResponse>>> agregarPlatoOrden(@PathVariable Integer pedidoId) {
        ResultadoResponse<List<DetallePedidoResponse>> lista = pedidoService.obtenerDetallePorPedido(pedidoId);
        if (lista.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(lista);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(lista);
        }
    }

    @PutMapping("/ent/{idDetalle}")
    public ResponseEntity<ResultadoResponse<DetallePedido>> cambiarEstadoEntregado(@PathVariable Integer idDetalle){
        ResultadoResponse<DetallePedido> cambiado = pedidoService.cambiarEstadoAEntregado(idDetalle);
        if (cambiado.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(cambiado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cambiado);
        }
    }

    @PutMapping("/can/{idDetalle}")
    public ResponseEntity<ResultadoResponse<DetallePedido>> cambiarEstadoCancelado(@PathVariable Integer idDetalle){
        ResultadoResponse<DetallePedido> cambiado = pedidoService.cambiarEstadoACancelado(idDetalle);
        if (cambiado.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(cambiado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cambiado);
        }
    }

    @GetMapping("/cliente/{idPedido}")
    public ResponseEntity<ResultadoResponse<DetallePedidoUsuarioResponse>>obtenerCliente(@PathVariable  Integer idPedido){
        ResultadoResponse<DetallePedidoUsuarioResponse> cliente = pedidoService.obtenerUsuarioPorPedidoReserva(idPedido);

        if (cliente.isValor()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cliente);
        }
    }
}

