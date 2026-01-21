package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEventoRepository extends JpaRepository<Evento,Integer> {
}
