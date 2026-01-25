package com.koroFoods.qualificationService.service;

import com.koroFoods.qualificationService.repository.ICalificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalificacionService {

    private final ICalificacionRepository calificacionRepository;
}
