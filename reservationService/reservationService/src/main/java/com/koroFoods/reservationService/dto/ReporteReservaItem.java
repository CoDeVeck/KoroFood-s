package com.koroFoods.reservationService.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ReporteReservaItem {

	private Integer idReserva;
    private String nombreCliente;
    private String emailCliente;
    private String telefonoCliente;
    private Integer numeroMesa;
    private String zona;
    private LocalDateTime fechaHora;
    private String estado;
    private String estadoDescripcion;
    private String metodoPago;
    private BigDecimal montoDeposito;
}
