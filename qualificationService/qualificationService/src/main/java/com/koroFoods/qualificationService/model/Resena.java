package com.koroFoods.qualificationService.model;

import com.koroFoods.qualificationService.enums.EstadoResena;
import com.koroFoods.qualificationService.enums.TipoEntidad;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_RESENA")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESENA")
    private Integer idResena;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ENTIDAD")
    private TipoEntidad tipoEntidad;

    @Column(name = "ID_ENTIDAD")
    private Integer idEntidad;

    @Column(name = "CALIFICACION")
    private int calificacion;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoResena estado;
}
