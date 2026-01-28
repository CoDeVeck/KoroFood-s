package com.koroFoods.reservationService.service;

import com.koroFoods.reservationService.dto.ReservaDtoFeing;
import com.koroFoods.reservationService.dto.ResultadoResponse;
import com.koroFoods.reservationService.feign.PedidoFeignClient;
import com.koroFoods.reservationService.feign.UsuarioFeignClient;
import com.koroFoods.reservationService.model.Reserva;
import com.koroFoods.reservationService.repository.IReservaRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private final IReservaRepository reservaRepository;
	private final UsuarioFeignClient usuarioFeignClient;
	private final PedidoFeignClient pedidoFeignClient;
	
	public ResultadoResponse<ReservaDtoFeing> getReservationByID(String codigo) {
	    Optional<Reserva> optionalReserva = reservaRepository.findReservaAsistidaById(codigo);

	    if (optionalReserva.isEmpty()) {
	        return ResultadoResponse.error(
	            "Reserva no encontrada, intente con otro código por favor"
	        );
	    }

	    Reserva reserva = optionalReserva.get();
	    
	    try {
	        var pedidoResponse = pedidoFeignClient.getPedidoByReservaId(reserva.getIdReserva());
	        
	        if (pedidoResponse.isValor() && pedidoResponse.getData() != null) {
	            return ResultadoResponse.error(
	                "Esta reserva ya tiene un pedido asociado."
	            );
	        }
	    } catch (FeignException.NotFound e) {
	        
	    } catch (Exception e) {
	        return ResultadoResponse.error(
	            "Error al validar el pedido de la reserva: " + e.getMessage()
	        );
	    }

	    var usuario = usuarioFeignClient.getUsuarioById(reserva.getIdUsuario());

	    if (!usuario.isValor() || usuario.getData() == null) {
	        return ResultadoResponse.error(
	            "El usuario con ID " + reserva.getIdUsuario() + " no existe"
	        );
	    }

	    ReservaDtoFeing dto = new ReservaDtoFeing();
	    dto.setIdReserva(reserva.getIdReserva());
	    dto.setEstado(reserva.getEstado().toString());
	    dto.setIdUsuario(reserva.getIdUsuario());
	    dto.setMesa(reserva.getIdMesa());
	    dto.setNombreCompletoUsuario(
	        usuario.getData().getNombres() + " " +
	        usuario.getData().getApePaterno() + " " +
	        usuario.getData().getApeMaterno()
	    );

	    return ResultadoResponse.success("Reserva encontrada", dto);
	}

}
