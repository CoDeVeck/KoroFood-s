package com.koroFoods.eventService.repository;

import com.koroFoods.eventService.model.Tematica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITematicaRepository extends JpaRepository<Tematica,Integer> {
}
