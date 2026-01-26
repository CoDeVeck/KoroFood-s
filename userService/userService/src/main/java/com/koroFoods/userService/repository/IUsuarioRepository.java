package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository  extends JpaRepository<Usuario, Integer> {


    Optional<Usuario> findByCorreo(String correoUsu);
    Optional<Usuario> findByTelefono(String telefonoUsu);
    Optional<Usuario> findByNroDoc(String documentoUsu);
    Optional<Usuario> findByApePaterno(String apePatUsu);
    Optional<Usuario> findByApeMaterno(String apeMatUsu);
}
