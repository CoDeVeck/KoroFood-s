package com.koroFoods.tableService.service;

import com.koroFoods.tableService.repository.IMesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final IMesaRepository mesaRepository;
}
