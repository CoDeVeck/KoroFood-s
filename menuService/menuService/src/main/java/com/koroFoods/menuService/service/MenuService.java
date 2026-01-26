package com.koroFoods.menuService.service;

import com.koroFoods.menuService.dto.PlatoDtoFeign;
import com.koroFoods.menuService.dto.ResultadoResponse;
import com.koroFoods.menuService.model.Plato;
import com.koroFoods.menuService.repository.IMenuRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final IMenuRepository menuRepository;
    
    public ResultadoResponse<List<PlatoDtoFeign>> getAllDish() {
        List<Plato> platos = menuRepository.findAll(); 
        List<PlatoDtoFeign> dtos = platos.stream().map(plato -> {
        	PlatoDtoFeign dto = new PlatoDtoFeign();
        	dto.setIdPlato(plato.getIdPlato());
        	dto.setNombre(plato.getNombre());
        	dto.setTipoPlato(plato.getTipoPlato().toString());
        	dto.setImagen(plato.getImagen());
            return dto;
        }).toList();
        return ResultadoResponse.success("Eventos encontrados", dtos);
    }
	// Método para el feign de la reseña
    public ResultadoResponse<PlatoDtoFeign> getDishById(Integer id){
    	Plato dish = menuRepository.findById(id).
    			orElseThrow(( )-> new RuntimeException("Plato no encontrado"));
    	
    	PlatoDtoFeign dto = new PlatoDtoFeign();
    	dto.setIdPlato(dish.getIdPlato());
    	dto.setNombre(dish.getNombre());
    	dto.setTipoPlato(dish.getTipoPlato().toString());
    	dto.setImagen(dish.getImagen());
    	
    	return ResultadoResponse.success("Plato encontrado", dto);
    }
}
