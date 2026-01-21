package com.koroFoods.menuService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.koroFoods.menuService.enums.TipoPlato;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_PLATO")
public class Plato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="")
    private Integer idPlato;

    @Column(name="")
    private String nombre;

    @Column(name="")
    private BigDecimal precio;

    @Column(name="")
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name="")
    private TipoPlato tipoPlato;

    @Column(name="")
    private String imagen;

    @JsonIgnore
    @Transient
    private MultipartFile imagenMultipart; // para la subida de imagens
}
