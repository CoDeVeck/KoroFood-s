package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.Tematica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITematicaRepository extends JpaRepository<Tematica,Integer> {
}
