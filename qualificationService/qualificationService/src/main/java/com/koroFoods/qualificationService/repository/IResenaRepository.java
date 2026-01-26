package com.koroFoods.qualificationService.repository;

import com.koroFoods.qualificationService.model.Resena;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IResenaRepository extends JpaRepository<Resena, Integer> {
	List<Resena> findByIdUsuario(Integer idUsuario);
}
