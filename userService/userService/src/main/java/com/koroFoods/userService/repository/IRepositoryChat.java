package com.koroFoods.userService.repository;

import com.koroFoods.userService.model.document.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IRepositoryChat extends MongoRepository<Chat, String> {

    //Obtenemos el historial de chats que se manda 1-1
    List<Chat> findByEmisorIdOrReceptorId(Integer idEmisor, Integer idReceptor);

}
