package com.koroFoods.eventService.service;

import com.koroFoods.eventService.repository.ITematicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TematicaService {

    private final ITematicaRepository tematicaRepository;
}
