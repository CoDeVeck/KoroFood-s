package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.dto.UsuarioDtoFeign;
import com.koroFoods.userService.model.Rol;
import com.koroFoods.userService.model.Usuario;
import com.koroFoods.userService.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService  {

    private final IUsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Optional<Usuario> obtenerDatosCliente(String correo){
        return  usuarioRepository.findByCorreo(correo);
    }


    public ResultadoResponse<Usuario> registrarUsuario(Usuario usuario) {

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            return ResultadoResponse.error("El correo ingresado ya existe, elige otro");
        }

        if (usuarioRepository.findByTelefono(usuario.getTelefono()).isPresent()) {
            return ResultadoResponse.error("El teléfono ingresado ya existe, elige otro");
        }

        if (usuarioRepository.findByNroDoc(usuario.getNroDoc()).isPresent()) {
            return ResultadoResponse.error("El N°: " + usuario.getNroDoc() + " ya fue registrado, elige otro");
        }

        if (usuarioRepository.findByApePaterno(usuario.getApePaterno()).isPresent() &&
            usuarioRepository.findByApeMaterno(usuario.getApeMaterno()).isPresent()) {
            return ResultadoResponse.error("Los apellidos ingresados ya fueron registrados, elige otro");
        }

        Rol rolDefinido = new Rol();
        rolDefinido.setIdRol(4);

        usuario.setClave(bCryptPasswordEncoder.encode(usuario.getClave()));
        usuario.setRol(rolDefinido);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        return ResultadoResponse.success("El usuario fue creado correctamente", nuevoUsuario);
    }

    
    // Método para el feign de la reseña
    public ResultadoResponse<UsuarioDtoFeign> getUsuarioByIdFeign(Integer id){
    	Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UsuarioDtoFeign dto = new UsuarioDtoFeign();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombres(usuario.getNombres());
        dto.setApePaterno(usuario.getApePaterno());
        dto.setApeMaterno(usuario.getApeMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setImagen(usuario.getImagen());

        return ResultadoResponse.success("Usuario encontrado", dto);
    }
}
