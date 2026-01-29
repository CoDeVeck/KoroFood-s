package com.koroFoods.reservationService.feign;

import lombok.Data;

@Data
public class MesaFeign {
    private Integer idMesa;
    private int numeroMesa;
    private int capacidad;
    private String zona;
    private String estado;
}
