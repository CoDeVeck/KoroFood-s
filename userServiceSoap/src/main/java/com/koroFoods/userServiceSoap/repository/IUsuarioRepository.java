package com.koroFoods.userServiceSoap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.koroFoods.userServiceSoap.model.Usuario;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer>{
Optional<Usuario> findByCorreo(String correo);
    
    Optional<Usuario> findByNroDoc(String nroDoc);
    
    List<Usuario> findByRol_IdRol(Integer idRol);

    List<Usuario> findByActivo(Boolean activo);

    List<Usuario> findByRol_IdRolAndActivo(Integer idRol, Boolean activo);

    List<Usuario> findByRol_IdRolIn(List<Integer> roles);

    List<Usuario> findByRol_IdRolInAndActivo(List<Integer> roles, Boolean activo);
}
