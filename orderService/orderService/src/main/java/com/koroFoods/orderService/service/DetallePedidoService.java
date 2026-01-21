package com.koroFoods.orderService.service;

import com.koroFoods.orderService.repository.IDetallePedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetallePedidoService {

    private final IDetallePedidoRepository detallePedidoRepository;
}
