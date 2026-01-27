package com.koroFoods.orderService.dto;

import java.util.List;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Integer idMesa;
    private Integer idUsuario;
    private List<DetallePedidoRequestDTO> detalles;
}

