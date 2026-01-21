package com.koroFoods.paymentService.model;

import com.koroFoods.paymentService.enums.EstadoPago;
import com.koroFoods.paymentService.enums.MetodoPago;
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
@Table(name = "TB_PAGO")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Integer idPago;

    @Column(name = "ID_RESERVA")
    private Integer idReserva;

    @Column(name = "ID_PEDIDO")
    private Integer idPedido;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora;

    @Column(name = "MONTO")
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "METODO_PAGO")
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoPago estado;
}
