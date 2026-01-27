package com.koroFoods.reservationService.repository;

import com.koroFoods.reservationService.model.Reserva;

import feign.Param;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IReservaRepository extends JpaRepository<Reserva, Integer> {

	@Query("""
			    SELECT r FROM Reserva r
			    WHERE r.estado = 'ASI'
			      AND r.idReserva = :id
			""")
	Optional<Reserva> findReservaAsistidaById(@Param("id") Integer id);
}
