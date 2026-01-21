package com.koroFoods.tableService.repository;

import com.koroFoods.tableService.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMesaRepository extends JpaRepository<Mesa, Integer> {
}
