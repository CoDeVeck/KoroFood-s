package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.EventoMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEventoMesaRepository extends JpaRepository<EventoMesa,Integer> {
}
