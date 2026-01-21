package com.koroFoods.userService.service;

import com.koroFoods.userService.repository.IDistritoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistritoService {

    private final IDistritoRepository distritoRepository;
}
