package com.koroFoods.qualificationService.service;

import com.koroFoods.qualificationService.repository.IResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final IResenaRepository resenaRepository;
}
