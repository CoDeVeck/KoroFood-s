package com.koroFoods.userService.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtilInyect, UserDetailsService userDetailsServiceInyect) {
        this.jwtUtil = jwtUtilInyect;
        this.userDetailsService = userDetailsServiceInyect;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if ("/auth/login".equals(path) ||
                "/auth/register".equals(path) ||
                path.startsWith("/ws") ||
                path.equals("/distrito/list") ||
                path.equals("/auth/github") ||
                path.equals("/auth/google") ||
                path.equals("/auth/social/register") ||
                path.equals("/chatbot/conversacion")

        ) {
            filterChain.doFilter(request, response);
            return;
        }
       

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            if (!token.isBlank()) {
                try {
                    userName = jwtUtil.obtenerUsuarioAndToken(token);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token JWT inválido o mal formado");
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token JWT no proporcionado");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No autorizado: falta token");
            return;
        }
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

            System.out.println("--- DEBUG ROLES ---");
            userDetails.getAuthorities().forEach(x -> System.out.println("Authority: " + x.getAuthority()));
            System.out.println("----------------------");

            if (jwtUtil.validarToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }


}
