package com.koroFoods.qualificationService.feign;

import lombok.Data;

@Data
public class UsuarioPublicoDTO {
    private Integer idUsuario;
    private String nombreCompleto;
    private String imagen;
}
