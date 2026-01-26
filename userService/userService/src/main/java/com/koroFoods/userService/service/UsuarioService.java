package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.ResultadoResponse;
import com.koroFoods.userService.dto.UsuarioDtoFeign;
import com.koroFoods.userService.model.Usuario;
import com.koroFoods.userService.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;
    
    
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
