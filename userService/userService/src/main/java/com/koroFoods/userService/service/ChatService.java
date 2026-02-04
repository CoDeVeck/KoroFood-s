package com.koroFoods.userService.service;

import com.koroFoods.userService.model.document.Chat;
import com.koroFoods.userService.repository.IRepositoryChat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final IRepositoryChat repositoryChat;

    List<Chat> listaDeChats(Integer emisorId, Integer idRecepcionista){
        return repositoryChat.findByEmisorIdOrReceptorId(emisorId,idRecepcionista);
    }
}
