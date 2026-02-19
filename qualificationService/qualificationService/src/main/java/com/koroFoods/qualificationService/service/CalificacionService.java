package com.koroFoods.qualificationService.service;

import com.koroFoods.qualificationService.dto.ResenaListResponse;
import com.koroFoods.qualificationService.dto.ResenaRequest;
import com.koroFoods.qualificationService.dto.ResultadoResponse;
import com.koroFoods.qualificationService.dto.response.GraficoSeisData;
import com.koroFoods.qualificationService.dto.response.GraficoSeisList;
import com.koroFoods.qualificationService.enums.EstadoResena;
import com.koroFoods.qualificationService.enums.TipoEntidad;
import com.koroFoods.qualificationService.feign.*;
import com.koroFoods.qualificationService.model.Calificacion;
import com.koroFoods.qualificationService.repository.ICalificacionRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalificacionService {
    private final ICalificacionRepository resenaRepository;
    private final PlatoFeignClient platoFeignClient;
    private final EventoFeignClient eventoFeignClient;
    private final UsuarioFeignClient usuarioFeignClient;
    
    public ResultadoResponse<List<ResenaListResponse>> listarResenas() {
        List<Calificacion> list = resenaRepository.findAll();

        List<ResenaListResponse> listResponse = list.stream()
                .map(resena -> convertirAResenaListResponse(resena, true)) 
                .collect(Collectors.toList());

        return ResultadoResponse.success("Reseñas listadas correctamente", listResponse);
    }

    public ResultadoResponse<List<ResenaListResponse>> obtenerResenasPorUsuario(Integer idUsuario) {
        List<Calificacion> resenas = resenaRepository.findByIdUsuario(idUsuario);

        List<ResenaListResponse> listResponse = resenas.stream()
                .map(resena -> convertirAResenaListResponse(resena, false)) 
                .collect(Collectors.toList());

        return ResultadoResponse.success("Reseñas del usuario listadas correctamente", listResponse);
    }

    
    public ResultadoResponse<Calificacion> crearResena(ResenaRequest req) {
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
        boolean yaExiste = resenaRepository.existsByIdUsuarioAndTipoEntidadAndIdEntidad(
                req.getIdUsuario(),
                req.getTipoEntidad(),
                req.getIdEntidad()
        );
        
        if (yaExiste) {
            return ResultadoResponse.error("Ya enviaste una calificación sobre este " +
                    (req.getTipoEntidad() == TipoEntidad.PLATO ? "plato" : "evento"));
        }
        Calificacion r = new Calificacion();
        r.setIdUsuario(req.getIdUsuario());
        r.setTipoEntidad(req.getTipoEntidad());
        r.setIdEntidad(req.getIdEntidad());
        r.setPuntuacion(req.getCalificacion());
        r.setComentario(req.getComentario());
        r.setFechaRegistro(LocalDateTime.now());
        r.setEstado(EstadoResena.ACT);

        resenaRepository.save(r);

        return ResultadoResponse.success("Reseña registrada correctamente", r);
    }

    private ResenaListResponse convertirAResenaListResponse(Calificacion resena, boolean publico) {
        ResenaListResponse response = new ResenaListResponse();
        response.setIdResena(resena.getIdCalificacion());
        response.setIdUsuario(resena.getIdUsuario());
        response.setIdEntidad(resena.getIdEntidad());
        response.setCalificacion(resena.getPuntuacion());
        response.setComentario(resena.getComentario());

        try {
            if(publico) {
                // Endpoint público SIN token
                ResultadoResponse<UsuarioPublicoDTO> usuario = usuarioFeignClient.getUserByIdNoauth(resena.getIdUsuario());
                
                if(usuario != null && usuario.isValor() && usuario.getData() != null) {
                    response.setNombreUsuarioCompleto(usuario.getData().getNombreCompleto());
                    response.setImagenUsuario(usuario.getData().getImagen());
                } else {
                    response.setNombreUsuarioCompleto("Usuario no disponible");
                    response.setImagenUsuario(null);
                }
            } else {
                // Endpoint CON token
                ResultadoResponse<UsuarioFeign> usuario = usuarioFeignClient.getUsuarioById(resena.getIdUsuario());
                
                if(usuario != null && usuario.isValor() && usuario.getData() != null) {
                    response.setNombreUsuarioCompleto(
                        usuario.getData().getNombres() + " " +
                        usuario.getData().getApePaterno() + " " +
                        usuario.getData().getApeMaterno()
                    );
                    response.setImagenUsuario(usuario.getData().getImagen());
                } else {
                    response.setNombreUsuarioCompleto("Usuario no disponible");
                    response.setImagenUsuario(null);
                }
            }
        } catch (FeignException e) {
            // ⚠️ AGREGA LOGS PARA VER EL ERROR REAL
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
            
            response.setImagenUsuario(null);
            response.setNombreUsuarioCompleto(publico ? "Usuario anónimo" : "Usuario no disponible");
        }

        try {
            switch (resena.getTipoEntidad()) {
                case PLATO -> {
                    var plato = platoFeignClient.getDishById(resena.getIdEntidad());
                    if(plato != null && plato.getData() != null) {
                        response.setImagenEntidad(plato.getData().getImagen());
                        response.setNombreEntidad(plato.getData().getNombre());
                    }
                }
                case EVENTO -> {
                    var evento = eventoFeignClient.getEventById(resena.getIdEntidad());
                    if(evento != null && evento.getData() != null) {
                        response.setImagenEntidad(evento.getData().getImagen());
                        response.setNombreEntidad(evento.getData().getNombre());
                    }
                }
            }
        } catch (FeignException e) {
            System.err.println("Error al obtener entidad: " + e.getMessage());
            response.setImagenEntidad(null);
            response.setNombreEntidad("Entidad no disponible");
        }

        return response;
    }

    public ResultadoResponse<List<GraficoSeisList>> graficoSeisList(Integer mes){

        List<GraficoSeisData> data = resenaRepository.graficoSeisList(mes);
        List<GraficoSeisList> list = new ArrayList<>();


        for(var plato : data){
            ResultadoResponse<PlatoFeign> platoFeign = platoFeignClient.getDishById(plato.getIdEntidad());
            var platoData = platoFeign.getData();

            list.add(new GraficoSeisList(
                    plato.getIdEntidad(),
                    plato.getPromedio(),
                    plato.getTotal(),
                    platoData.getNombre()
            ));

        }

        if (!list.isEmpty()){
            return ResultadoResponse.success("Se obtuvo la lista: ", list);
        }

        return ResultadoResponse.error("No hay datos para la lista", list);
    }
}
