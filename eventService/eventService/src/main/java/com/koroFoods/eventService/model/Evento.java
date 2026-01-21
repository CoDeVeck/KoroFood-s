package com.koroFoods.eventService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_EVENTO")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Integer idEvento;

    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "DESCRIPCION")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "TIPO_TEMATICA")
    private Tematica tematica;

    @Column(name = "FECHA")
    private LocalDate fecha;

    @Column(name = "HORA")
    private LocalTime hora;

    @Column(name = "PRECIO")
    private BigDecimal precio;

    @Column(name = "AFORO")
    private Integer aforo;

    @Column(name = "IMAGEN")
    private String imagen;

    @JsonIgnore
    @Transient
    private MultipartFile imagenMultipart; // para la subida de imagens
}
