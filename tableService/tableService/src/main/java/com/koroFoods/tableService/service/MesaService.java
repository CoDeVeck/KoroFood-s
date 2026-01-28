package com.koroFoods.tableService.service;

import com.koroFoods.tableService.dto.MesaDtoFeign;
import com.koroFoods.tableService.dto.ResultadoResponse;
import com.koroFoods.tableService.model.Mesa;
import com.koroFoods.tableService.repository.IMesaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final IMesaRepository mesaRepository;
    
    // Métodos para el feign en order
    public ResultadoResponse<MesaDtoFeign> getTableById(Integer id){
    	Mesa mesa = mesaRepository.findById(id).orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
    	MesaDtoFeign dto = new MesaDtoFeign();
    	dto.setIdMesa(mesa.getIdMesa());
    	dto.setNumeroMesa(mesa.getNumeroMesa());
    	dto.setCapacidad(mesa.getCapacidad());
    	dto.setTipo(mesa.getZona().toString());
    	dto.setEstado(mesa.getEstado().toString());
    	return ResultadoResponse.success("Mesa encontrada", dto);
    }
}
