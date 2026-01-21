package com.koroFoods.eventService.service;

import com.koroFoods.eventService.repository.IEventoMesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventoMesaService {

    private final IEventoMesaRepository eventoMesaRepository;
}
