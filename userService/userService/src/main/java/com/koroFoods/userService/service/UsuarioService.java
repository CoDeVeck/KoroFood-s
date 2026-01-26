package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.response.PerfilUsuarioResponse;
import com.koroFoods.userService.dto.response.ResultadoResponse;
import com.koroFoods.userService.model.Rol;
import com.koroFoods.userService.model.Usuario;
import com.koroFoods.userService.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return User.withUsername(u.getCorreo())
                .password("{noop}" + u.getClave())
                .roles(u.getRol().getDescripcion().replace("ROLE_",""))
                .build();
    }


    public PerfilUsuarioResponse obtenerPerfil(Integer idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Error al buscar al usuario: " + idUsuario));

        PerfilUsuarioResponse perfil = new PerfilUsuarioResponse();

        perfil.setIdUsuario(usuario.getIdUsuario());
        perfil.setNombres(usuario.getNombres());
        perfil.setApePaterno(usuario.getApePaterno());
        perfil.setApeMaterno(usuario.getApeMaterno());
        perfil.setCorreo(usuario.getCorreo());
        perfil.setImagen(usuario.getImagen());
        perfil.setDireccion(usuario.getDireccion());
        perfil.setTelefono(usuario.getTelefono());
        perfil.setFechaRegistro(usuario.getFechaRegistro().toString());

        return perfil;
    }

    public ResultadoResponse registrarUsuario(Usuario
                                                      usuario){
        ResultadoResponse resultado = new ResultadoResponse();

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("El correo ingresado ya existe elige otro");
        }

        if (usuarioRepository.findByTelefono(usuario.getTelefono()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("El telefono ingresado ya existe elige otro");
        }

        if (usuarioRepository.findByNroDoc(usuario.getNroDoc()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("El N°: " + usuario.getNroDoc()+ " ya fue registrado elige otro");
        }

        if (usuarioRepository.findByApePaterno(usuario.getApePaterno()).isPresent()
                && usuarioRepository.findByApeMaterno(usuario.getApeMaterno()).isPresent()){
            resultado.setValor(false);
            resultado.setMensaje("Los apellidos ingresados ya fueron registrados elige otro");
        }

        Rol rolDefindo = new Rol();
        rolDefindo.setIdRol(2);

        usuario.setRol(rolDefindo);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado(true);

        usuarioRepository.save(usuario);
        resultado.setValor(true);
        resultado.setMensaje("El usuario fue creado correctamente");

        return resultado;
    }
}
