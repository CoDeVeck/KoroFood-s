package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.EventoMesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEventoMesaRepository extends JpaRepository<EventoMesa,Integer> {
}
