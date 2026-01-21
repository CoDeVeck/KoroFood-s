package com.koroFoods.paymentService.service;

import com.koroFoods.paymentService.repository.IPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final IPagoRepository pagoRepository;
}
