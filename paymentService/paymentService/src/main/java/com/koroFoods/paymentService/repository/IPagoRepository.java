package com.koroFoods.paymentService.repository;

import com.koroFoods.paymentService.enums.EstadoPago;
import com.koroFoods.paymentService.model.Pago;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPagoRepository extends JpaRepository<Pago, Integer> {
	
	 Optional<Pago> findByReferenciaPago(String referenciaPago);

    List<Pago> findByIdUsuario(Integer idUsuario);

    List<Pago> findByIdReserva(Integer idReserva);

    List<Pago> findByIdPedido(Integer idPedido);

    List<Pago> findByEstado(EstadoPago estado);

    
    boolean existsByHashImagen(String hashImagen);
    
    boolean existsByCodigoOperacion(String codigoOperacion);
    
    Optional<Pago> findByHashImagen(String hashImagen);
    
    Optional<Pago> findByCodigoOperacion(String codigoOperacion);
}