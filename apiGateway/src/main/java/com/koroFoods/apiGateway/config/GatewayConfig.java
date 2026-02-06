package com.koroFoods.apiGateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("usuario-service", r -> r
                        .path("/auth/**", "/distrito/**", "/cliente/**")
                        .uri("lb://userService")
                )
                //para el uso de webSockets
                .route("usuario-service-ws", r -> r
                        .path("/ws/**")
                        .uri("lb:ws://userService")
                )
                .route("evento-service", r -> r
                        .path("/eventos/**", "/evento/feign/**")
                        .uri("lb://eventService")
                )
                .route("menu-service", r -> r
                        .path("/menu/**", "/menu/feign/**")
                        .uri("lb://menuService")
                )
                .route("qualification-service", r -> r
                        .path("/calificacion/**")
                        .uri("lb://qualificationService")
                )
                .build();
    }

}