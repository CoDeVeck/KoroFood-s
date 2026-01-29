package com.koroFoods.tableService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.tableService.service.MesaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mesa")
@RequiredArgsConstructor
public class MesaController {
	
	private final MesaService mesaService;
	
}
