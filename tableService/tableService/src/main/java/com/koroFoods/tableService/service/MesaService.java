package com.koroFoods.tableService.service;

import com.koroFoods.tableService.dto.MesaDtoFeign;
import com.koroFoods.tableService.dto.ResultadoResponse;
import com.koroFoods.tableService.enums.EstadoMesa;
import com.koroFoods.tableService.enums.Zona;
import com.koroFoods.tableService.model.Mesa;
import com.koroFoods.tableService.repository.IMesaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MesaService {

	private final IMesaRepository mesaRepository;

    public ResultadoResponse<MesaDtoFeign> getTableById(Integer id){
        Mesa mesa = mesaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        
        MesaDtoFeign dto = convertirAMesaFeign(mesa);
        return ResultadoResponse.success("Mesa encontrada", dto);
    }

    public ResultadoResponse<List<MesaDtoFeign>> obtenerMesasPorZona(Zona zona) {

        List<MesaDtoFeign> mesas = mesaRepository.findByZonaAndEstadoNot(zona, EstadoMesa.MANTENIMIENTO)
                .stream()
                .map(this::convertirAMesaFeign)
                .toList();

        return ResultadoResponse.success(
                "Mesas encontradas en zona " + zona,
                mesas
        );
    }
    
    public ResultadoResponse<List<MesaDtoFeign>> obtenerMesasPorIds(List<Integer> ids) {
        List<MesaDtoFeign> mesas = mesaRepository.findAllById(ids)
                .stream()
                .map(this::convertirAMesaFeign)
                .collect(Collectors.toList());
        
        return ResultadoResponse.success("Mesas encontradas", mesas);
    }
    
    private MesaDtoFeign convertirAMesaFeign(Mesa mesa) {
        MesaDtoFeign dto = new MesaDtoFeign();
        dto.setIdMesa(mesa.getIdMesa());
        dto.setNumeroMesa(mesa.getNumeroMesa());
        dto.setCapacidad(mesa.getCapacidad());
        dto.setTipo(mesa.getZona().toString());
        dto.setEstado(mesa.getEstado().toString());
        return dto;
    }
}
