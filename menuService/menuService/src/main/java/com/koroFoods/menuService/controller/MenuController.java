package com.koroFoods.menuService.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.menuService.dto.PlatoDtoFeign;
import com.koroFoods.menuService.dto.ResultadoResponse;
import com.koroFoods.menuService.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	// Endpoint para el feign de la reseña
	@GetMapping
	public ResponseEntity<ResultadoResponse<List<PlatoDtoFeign>>> list(){
		ResultadoResponse<List<PlatoDtoFeign>> resultado = menuService.getAllDish();
		
		if(resultado.isValor()) {
			return ResponseEntity.status(HttpStatus.OK).body(resultado);
		}else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
	    }
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ResultadoResponse<PlatoDtoFeign>> getDishById(@PathVariable Integer id) {
		ResultadoResponse<PlatoDtoFeign> dish = menuService.getDishById(id);
		return ResponseEntity.ok(dish);
	}
	
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> downloadMenuPdf() {
	    try {
	        ResultadoResponse<List<PlatoDtoFeign>> resultado = menuService.getAllDish();
	        
	        if (!resultado.isValor() || resultado.getData() == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	        
	        byte[] pdfBytes = menuService.generateMenuPdf(resultado.getData());
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(
	            ContentDisposition.builder("attachment")
	                .filename("KoroFood-Menu-" + LocalDate.now() + ".pdf")
	                .build()
	        );
	        
	        return ResponseEntity.ok()
	            .headers(headers)
	            .body(pdfBytes);
	            
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}
