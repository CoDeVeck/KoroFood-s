package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.Evento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventoRepository extends JpaRepository<Evento, Integer> {

	List<Evento> findByActivoTrue();

	Optional<Evento> findByIdEventoAndActivoTrue(Integer id);

	List<Evento> findByTematica_IdTematicaAndActivoTrue(Integer idTematica);

	@Query("SELECT e FROM Evento e WHERE e.fecha >= :fechaActual AND e.activo = true")
	List<Evento> findEventosFuturos(LocalDateTime fechaActual);

}
