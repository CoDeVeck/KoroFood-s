package com.koroFoods.userService.controller;

import com.koroFoods.userService.dto.request.UpdatePasswordRequest;
import com.koroFoods.userService.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final UsuarioService usuarioService;

    @PutMapping("/update/{id}")
    public ResponseEntity<?> actulizarPass(
            @PathVariable Integer id,
            @RequestBody UpdatePasswordRequest request
            ){
        try {

            usuarioService.actualizarPassword(request, id);
            return ResponseEntity.ok(
                    "Password actualizado correctamente");


        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(Map.of("Error", "No se actualizo la contraseña"));

        }
    }
}
