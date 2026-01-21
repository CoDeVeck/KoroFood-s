package com.koroFoods.tableService.model;

import com.koroFoods.tableService.enums.EstadoMesa;
import com.koroFoods.tableService.enums.TipoMesa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_MESA")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MESA")
    private Integer idMesa;

    @Column(name = "NUMERO_MESA")
    private int numeroMesa;

    @Column(name = "CAPACIDAD")
    private int capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO")
    private TipoMesa tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoMesa estado;
}
