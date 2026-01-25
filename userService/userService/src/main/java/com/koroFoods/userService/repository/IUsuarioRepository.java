package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository  extends JpaRepository<Usuario, Integer> {
}
