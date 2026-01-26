package com.koroFoods.menuService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.koroFoods.menuService.model.Plato;


public interface IMenuRepository extends JpaRepository<Plato,Integer> {
}
