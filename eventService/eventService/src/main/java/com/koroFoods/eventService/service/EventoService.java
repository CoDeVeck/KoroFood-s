package com.koroFoods.eventService.service;

import com.koroFoods.eventService.repository.IEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final IEventoRepository eventoRepository;
}
