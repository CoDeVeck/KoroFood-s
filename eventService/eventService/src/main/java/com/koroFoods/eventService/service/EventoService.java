package com.koroFoods.eventService.service;

import com.koroFoods.eventService.dto.EventoDtoFeign;
import com.koroFoods.eventService.dto.ResultadoResponse;
import com.koroFoods.eventService.model.Evento;
import com.koroFoods.eventService.repository.IEventoRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final IEventoRepository eventoRepository;
    
    public ResultadoResponse<List<EventoDtoFeign>> getAllEvents() {
        List<Evento> eventos = eventoRepository.findAll(); 
        List<EventoDtoFeign> dtos = eventos.stream().map(evento -> {
            EventoDtoFeign dto = new EventoDtoFeign();
            dto.setIdEvento(evento.getIdEvento());
            dto.setDescripcion(evento.getDescripcion());
            dto.setNombre(evento.getNombre());
            dto.setImagen(evento.getImagen());
            return dto;
        }).toList();
        return ResultadoResponse.success("Listado de Eventos", dtos);
    }

	// Método para el feign de la reseña
    public ResultadoResponse<EventoDtoFeign> getEventById(Integer id){
    	Evento evento = eventoRepository.findById(id).orElseThrow(()-> new RuntimeException("Evento no encontrado"));
    	
    	EventoDtoFeign dto = new EventoDtoFeign();
    	dto.setIdEvento(evento.getIdEvento());
    	dto.setDescripcion(evento.getDescripcion());
    	dto.setNombre(evento.getNombre());
    	dto.setImagen(evento.getImagen());
    	
    	return ResultadoResponse.success("Evento encontrado", dto);
    }
}
