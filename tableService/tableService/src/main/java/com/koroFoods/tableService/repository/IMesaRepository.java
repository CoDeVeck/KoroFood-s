package com.koroFoods.tableService.repository;

import com.koroFoods.tableService.enums.Zona;
import com.koroFoods.tableService.model.Mesa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface IMesaRepository extends JpaRepository<Mesa, Integer> {
	

	List<Mesa> findByZonaAndActivoTrue(Zona zona);

}
