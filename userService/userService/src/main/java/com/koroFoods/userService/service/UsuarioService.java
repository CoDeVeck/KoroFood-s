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

        usuario.setClave(bCryptPasswordEncoder.encode(usuario.getClave()));
        usuario.setRol(rolDefindo);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setActivo(true);

        usuarioRepository.save(usuario);
        resultado.setValor(true);
        resultado.setMensaje("El usuario fue creado correctamente");

        return resultado;
    }
}
