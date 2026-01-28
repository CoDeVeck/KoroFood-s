package com.koroFoods.reservationService.repository;

import com.koroFoods.reservationService.model.Reserva;

import feign.Param;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IReservaRepository extends JpaRepository<Reserva, Integer> {

	@Query("""
			    SELECT r FROM Reserva r
			    WHERE r.estado = 'ASISTIDA'
			      AND r.codigoVerificacion = :codigo
			""")
	Optional<Reserva> findReservaAsistidaById(@Param("codigo") String codigo);
}
