package com.koroFoods.reservationService.model;

import com.koroFoods.reservationService.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_RESERVA")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVA")
    private Integer idReserva;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "ID_MESA")
    private Integer idMesa;

    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoReserva estado;

    @Column(name = "MONTO")
    private BigDecimal monto;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;
}
