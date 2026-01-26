package com.koroFoods.qualificationService.service;

import com.koroFoods.qualificationService.dto.ResenaListResponse;
import com.koroFoods.qualificationService.dto.ResenaRequest;
import com.koroFoods.qualificationService.dto.ResultadoResponse;
import com.koroFoods.qualificationService.enums.EstadoResena;
import com.koroFoods.qualificationService.feign.EventoFeignClient;
import com.koroFoods.qualificationService.feign.PlatoFeignClient;
import com.koroFoods.qualificationService.feign.UsuarioFeign;
import com.koroFoods.qualificationService.feign.UsuarioFeignClient;
import com.koroFoods.qualificationService.model.Resena;
import com.koroFoods.qualificationService.repository.IResenaRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResenaService {
    private final IResenaRepository resenaRepository;
    private final PlatoFeignClient platoFeignClient;
    private final EventoFeignClient eventoFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;
    
    public ResultadoResponse<List<ResenaListResponse>> listarResenas() {
        List<Resena> list = resenaRepository.findAll();
        
        List<ResenaListResponse> listResponse = list.stream()
            .map(this::convertirAResenaListResponse)
            .collect(Collectors.toList());
        
        return ResultadoResponse.success("Reseñas listadas correctamente", listResponse);
    }
    
    public ResultadoResponse<List<ResenaListResponse>> obtenerResenasPorUsuario(Integer idUsuario) {
        List<Resena> resenas = resenaRepository.findByIdUsuario(idUsuario);
        
        if (resenas.isEmpty()) {
            return ResultadoResponse.error("No se encontraron reseñas para el usuario");
        }
        
        List<ResenaListResponse> listResponse = resenas.stream()
            .map(this::convertirAResenaListResponse)
            .collect(Collectors.toList());
        
        return ResultadoResponse.success("Reseñas del usuario listadas correctamente", listResponse);
    }
    
    public ResultadoResponse<Resena> crearResena(ResenaRequest req) {
        try {
            var usuario = usuarioFeignClient.getUsuarioById(req.getIdUsuario());
        } catch (FeignException.NotFound e) {
            return ResultadoResponse.error("El usuario con ID " + req.getIdUsuario() + " no existe");
        } catch (FeignException e) {
            return ResultadoResponse.error("Error al consultar el usuario: " + e.getMessage());
        }

        switch (req.getTipoEntidad()) {
            case PLATO -> {
                try {
                    var plato = platoFeignClient.getDishById(req.getIdEntidad());
                } catch (FeignException.NotFound e) {
                    return ResultadoResponse.error("El plato con ID " + req.getIdEntidad() + " no existe");
                } catch (FeignException e) {
                    return ResultadoResponse.error("Error al consultar el plato: " + e.getMessage());
                }
            }

            case EVENTO -> {
                try {
                    var evento = eventoFeignClient.getEventById(req.getIdEntidad());
                } catch (FeignException.NotFound e) {
                    return ResultadoResponse.error("El evento con ID " + req.getIdEntidad() + " no existe");
                } catch (FeignException e) {
                    return ResultadoResponse.error("Error al consultar el evento: " + e.getMessage());
                }
            }
        }

        Resena r = new Resena();
        r.setIdUsuario(req.getIdUsuario());
        r.setTipoEntidad(req.getTipoEntidad());
        r.setIdEntidad(req.getIdEntidad());
        r.setCalificacion(req.getCalificacion());
        r.setComentario(req.getComentario());
        r.setFechaRegistro(LocalDateTime.now());
        r.setEstado(EstadoResena.ACT);

        resenaRepository.save(r);

        return ResultadoResponse.success("Reseña registrada correctamente", r);
    }

    // Método para mapear el listado de las reseñas
    private ResenaListResponse convertirAResenaListResponse(Resena resena) {
        ResenaListResponse response = new ResenaListResponse();
        response.setIdResena(resena.getIdResena());
        response.setIdUsuario(resena.getIdUsuario());
        response.setIdEntidad(resena.getIdEntidad());
        response.setCalificacion(resena.getCalificacion());
        response.setComentario(resena.getComentario());

        try {
            ResultadoResponse<UsuarioFeign> usuario = usuarioFeignClient.getUsuarioById(resena.getIdUsuario());
            response.setImagenUsuario(usuario.getData().getImagen());
            response.setNombreUsuarioCompleto(usuario.getData().getNombres() + " "
                + usuario.getData().getApePaterno() + " "
                + usuario.getData().getApeMaterno());
        } catch (FeignException e) {
            response.setImagenUsuario(null);
            response.setNombreUsuarioCompleto("Usuario no disponible");
        }

        try {
            switch (resena.getTipoEntidad()) {
                case PLATO -> {
                    var plato = platoFeignClient.getDishById(resena.getIdEntidad());
                    response.setImagenEntidad(plato.getData().getImagen());
                    response.setNombreEntidad(plato.getData().getNombre()); 
                }
                case EVENTO -> {
                    var evento = eventoFeignClient.getEventById(resena.getIdEntidad());
                    response.setImagenEntidad(evento.getData().getImagen());
                    response.setNombreEntidad(evento.getData().getNombre()); 
                }
            }
        } catch (FeignException e) {
            response.setImagenEntidad(null);
            response.setNombreEntidad("Entidad no disponible");
        }

        return response;
    }

}
