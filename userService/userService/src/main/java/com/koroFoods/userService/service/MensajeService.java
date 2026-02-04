package com.koroFoods.userService.service;

import com.koroFoods.userService.model.document.Mensaje;
import com.koroFoods.userService.repository.IRepositoryMensaje;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final IRepositoryMensaje repositoryMensaje;


    public Page<Mensaje> listarMensajePorIdChat(String chatId, Pageable pageable) {
        return repositoryMensaje.findByChatIdOrderByFechaAsc(chatId, pageable);
    }



    public Mensaje guardarMensaje(Mensaje request) {
        Mensaje mensajeGuardado = new Mensaje();

        if (request == null) {
            throw new IllegalArgumentException("Mensaje vacío");
        }

        mensajeGuardado.setChatId(request.getChatId());
        mensajeGuardado.setEmisorId(request.getEmisorId());
        mensajeGuardado.setReceptorId(request.getReceptorId());
        mensajeGuardado.setContenido(request.getContenido());
        mensajeGuardado.setFechaMandado(LocalDateTime.now());
        mensajeGuardado.setLeido(false);

        return repositoryMensaje.save(mensajeGuardado);

    }
}
