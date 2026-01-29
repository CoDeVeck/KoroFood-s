package com.koroFoods.eventService.dtos;

import lombok.Data;

@Data
public class EventoDtoFeign {
    private Integer idEvento;
    private String nombre;
    private String descripcion;
    private String imagen;
}
