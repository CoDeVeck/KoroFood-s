package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository  extends JpaRepository<Usuario, Integer> {
}
