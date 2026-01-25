package com.koroFoods.menuService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koroFoods.menuService.model.Plato;

@Repository
public interface IMenuRepository extends JpaRepository<Plato,Integer> {
}
