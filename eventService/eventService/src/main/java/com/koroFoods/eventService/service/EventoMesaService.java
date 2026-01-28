package com.koroFoods.eventService.service;

import com.koroFoods.eventService.dtos.EventResponse;
import com.koroFoods.eventService.dtos.EventTableRequest;
import com.koroFoods.eventService.dtos.EventTableResponse;
import com.koroFoods.eventService.exception.BusinessException;
import com.koroFoods.eventService.exception.ResourceNotFoundException;
import com.koroFoods.eventService.model.Evento;
import com.koroFoods.eventService.model.EventoMesa;
import com.koroFoods.eventService.repository.IEventoMesaRepository;
import com.koroFoods.eventService.repository.IEventoRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventoMesaService {

    private final IEventoMesaRepository eventoMesaRepository;
    
    private final IEventoRepository eventoRepository;
    private final EventoService eventoService;
    
    @Transactional
    public EventTableResponse asignarMesaAEvento(EventTableRequest request) {
        validarFechas(request);

        Evento evento = eventoRepository.findById(request.getIdEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + request.getIdEvento()));

        if (eventoMesaRepository.existeSolapamientoMesaNuevo(
                request.getIdMesa(),
                request.getFechaDesde(),
                request.getFechaHasta())) {
            throw new BusinessException("La mesa ya está asignada en el rango de fechas especificado");
        }

        EventoMesa eventoMesa = new EventoMesa();
        eventoMesa.setEvento(evento);
        eventoMesa.setIdMesa(request.getIdMesa());
        eventoMesa.setFechaDesde(request.getFechaDesde());
        eventoMesa.setFechaHasta(request.getFechaHasta());
        eventoMesa.setActivo(true);

        EventoMesa guardado = eventoMesaRepository.save(eventoMesa);
        return mapearAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<EventTableResponse> listarTodos() {
        return eventoMesaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventTableResponse> listarActivos() {
        return eventoMesaRepository.findByActivoTrue().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventTableResponse> listarPorEvento(Integer idEvento) {
        return eventoMesaRepository.findByEvento_IdEventoAndActivoTrue(idEvento).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventTableResponse> listarPorMesa(Integer idMesa) {
        return eventoMesaRepository.findByIdMesaAndActivoTrue(idMesa).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventTableResponse buscarPorId(Integer id) {
        EventoMesa eventoMesa = eventoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventoMesa no encontrado con ID: " + id));
        return mapearAResponse(eventoMesa);
    }

    @Transactional
    public EventTableResponse actualizar(Integer id, EventTableRequest request) {
        EventoMesa eventoMesa = eventoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventoMesa no encontrado con ID: " + id));

        validarFechas(request);

        if (eventoMesaRepository.existeSolapamientoMesa(
                request.getIdMesa(),
                request.getFechaDesde(),
                request.getFechaHasta(),
                id)) {
            throw new BusinessException("La mesa ya está asignada en el rango de fechas especificado");
        }

        Evento evento = eventoRepository.findById(request.getIdEvento())
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + request.getIdEvento()));

        eventoMesa.setEvento(evento);
        eventoMesa.setIdMesa(request.getIdMesa());
        eventoMesa.setFechaDesde(request.getFechaDesde());
        eventoMesa.setFechaHasta(request.getFechaHasta());

        EventoMesa actualizado = eventoMesaRepository.save(eventoMesa);
        return mapearAResponse(actualizado);
    }

    @Transactional
    public void eliminar(Integer id) {
        EventoMesa eventoMesa = eventoMesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventoMesa no encontrado con ID: " + id));
        
        eventoMesa.setActivo(false);
        eventoMesaRepository.save(eventoMesa);
    }

    private void validarFechas(EventTableRequest request) {
        if (request.getFechaHasta().isBefore(request.getFechaDesde())) {
            throw new BusinessException("La fecha hasta debe ser posterior a la fecha desde");
        }
    }

    private EventTableResponse mapearAResponse(EventoMesa eventoMesa) {
        EventResponse eventoResponse = eventoService.buscarPorId(eventoMesa.getEvento().getIdEvento());

        return EventTableResponse.builder()
                .idEventoMesa(eventoMesa.getIdEventoMesa())
                .evento(eventoResponse)
                .idMesa(eventoMesa.getIdMesa())
                .fechaDesde(eventoMesa.getFechaDesde())
                .fechaHasta(eventoMesa.getFechaHasta())
                .activo(eventoMesa.getActivo())
                .build();
    }
}
