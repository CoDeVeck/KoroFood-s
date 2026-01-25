package com.koroFoods.qualificationService.repository;

import com.koroFoods.qualificationService.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICalificacionRepository extends JpaRepository<Calificacion, Integer> {
}
