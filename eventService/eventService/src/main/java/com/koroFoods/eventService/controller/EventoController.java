package com.koroFoods.eventService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.eventService.service.EventoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/evento")
@RequiredArgsConstructor
public class EventoController {
	private final EventoService eventoService;

}
