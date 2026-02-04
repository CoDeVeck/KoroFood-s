package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.document.Chat;
import com.koroFoods.userService.model.document.Mensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IRepositoryMensaje extends MongoRepository<Mensaje, String> {


    //Buscamos mensajes por el chat que estamos entrando 1,2,3 el historial de mensajes
    Page<Mensaje> findByChatIdOrderByFechaAsc(String chatId, Pageable pageable);


}
