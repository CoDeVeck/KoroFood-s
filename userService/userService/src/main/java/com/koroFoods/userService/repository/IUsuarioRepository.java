package com.koroFoods.userService.repository;

import com.koroFoods.userService.dto.response.PerfilClienteResponse;
import com.koroFoods.userService.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Repository
public interface IUsuarioRepository  extends JpaRepository<Usuario, Integer> {


    Optional<Usuario> findByCorreo(String correoUsu);
    Optional<Usuario> findByTelefono(String telefonoUsu);
    Optional<Usuario> findByNroDoc(String documentoUsu);
    Optional<Usuario> findByApePaterno(String apePatUsu);
    Optional<Usuario> findByApeMaterno(String apeMatUsu);


    @Query(value = """
            SELECT 
             CAST(u.ID_USUARIO AS INTEGER) as idUsuario,
             u.NOMBRES as nombres,
             CONCAT(u.APE_PATERNO , ' ' , u.APE_MATERNO) as apellidos,
             u.CORREO as correo,
             u.NRO_DOC as nroDoc,
             u.IMAGEN as imagen,
             u.DIRECCION as direccion,
             u.TELEFONO as telefono
             FROM TB_USUARIO u
             where u.ID_USUARIO = :idUsuario
            """,nativeQuery = true)
    PerfilClienteResponse obtenerPerfil(@Param("idUsuario") Integer idUsuario);
}
