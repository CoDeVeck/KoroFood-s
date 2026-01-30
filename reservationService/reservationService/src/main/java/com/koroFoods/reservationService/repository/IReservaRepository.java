package com.koroFoods.reservationService.repository;

import com.koroFoods.reservationService.model.Reserva;

import feign.Param;

import java.time.LocalDateTime;
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

	@Query(value = """
			    SELECT COUNT(*) > 0
			    FROM TB_RESERVA r
			    WHERE r.ID_MESA = :idMesa
			      AND r.ESTADO IN ('PAGADA', 'ASISTIDA')
			      AND r.FECHA_RESERVA < :fechaHasta
			      AND (
			            CASE
			                WHEN r.ID_EVENTO IS NOT NULL
			                    THEN r.FECHA_RESERVA + INTERVAL '3 hour'
			                ELSE
			                    r.FECHA_RESERVA + INTERVAL '2 hour'
			            END
			          ) > :fechaDesde
			""", nativeQuery = true)
	boolean existeSolapamientoReserva(@Param("idMesa") Integer idMesa, @Param("fechaDesde") LocalDateTime fechaDesde,
			@Param("fechaHasta") LocalDateTime fechaHasta);

}
