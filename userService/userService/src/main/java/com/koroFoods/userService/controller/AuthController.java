package com.koroFoods.userService.controller;

import com.koroFoods.userService.dto.request.LoginRequest;
import com.koroFoods.userService.service.CloudinaryService;
import com.koroFoods.userService.service.UsuarioService;
import com.koroFoods.userService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;


    @PostMapping("/login")
    public ResponseEntity<?>loginUsuario(@RequestBody LoginRequest loginRequest){

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(),
                            loginRequest.getClave())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

            String token = jwtUtil.generateToken(loginRequest.getCorreo(), roles);
            return ResponseEntity.ok(Map.of("Token: ", token));
        } catch (Exception e) {
            System.out.println(">>> ERROR EN AUTENTICACIÓN: " + e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
