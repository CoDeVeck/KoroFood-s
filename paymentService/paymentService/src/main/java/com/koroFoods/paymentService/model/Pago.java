package com.koroFoods.paymentService.model;

import com.koroFoods.paymentService.enums.EstadoPago;
import com.koroFoods.paymentService.enums.MetodoPago;
import com.koroFoods.paymentService.enums.TipoPago;

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PAGO")
    private TipoPago tipoPago;

    @Column(name = "MONTO")
    private BigDecimal monto;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "METODO_PAGO")
    private MetodoPago metodoPago;
    
    @Column(name = "FECHA_PAGO")
    private LocalDateTime fechaPago;;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO")
    private EstadoPago estado;
    
    @Column(name = "OBSERVACIONES")
    private String observaciones;
    
    @Column(name = "CODIGO_OPERACION", unique = true)
    private String codigoOperacion; // Código que ingresa el usuario después de pagar

    @Column(name = "REFERENCIA_PAGO", unique = true)
    private String referenciaPago; // ID único generado para el QR

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_EXPIRACION")
    private LocalDateTime fechaExpiracion; // QR válido por X tiempo

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoPago.PEN;
        // QR válido por 30 minutos
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(30);
    }
}
