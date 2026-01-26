package com.koroFoods.menuService.dto;

import lombok.Data;

@Data
public class PlatoDtoFeign {
    private Integer idPlato;
    private String nombre;
    private String tipoPlato;
    private String imagen;
}
