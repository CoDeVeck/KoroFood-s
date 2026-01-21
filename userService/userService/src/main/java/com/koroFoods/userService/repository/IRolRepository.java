package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRolRepository extends JpaRepository<Rol, Integer> {
}
