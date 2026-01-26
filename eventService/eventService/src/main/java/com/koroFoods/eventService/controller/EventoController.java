package com.koroFoods.eventService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.eventService.service.EventoService;
import com.koroFoods.eventService.dto.EventoDtoFeign;
import com.koroFoods.eventService.dto.ResultadoResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/evento")
@RequiredArgsConstructor
public class EventoController {
	private final EventoService eventoService;

	// Endpoint para el feign de la reseña
	@GetMapping
	public ResponseEntity<ResultadoResponse<List<EventoDtoFeign>>> list(){
		ResultadoResponse<List<EventoDtoFeign>> resultado = eventoService.getAllEvents();
		
		if(resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		}else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<EventoDtoFeign>> getEventhById(@PathVariable Integer id) {
		ResultadoResponse<EventoDtoFeign> event = eventoService.getEventById(id);
		return ResponseEntity.ok(event);
	}
}
