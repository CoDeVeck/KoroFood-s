package com.koroFoods.userService.service;

import com.koroFoods.userService.dto.GithubUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

public class GithubService {

    @Value("${github.clientId}")
    private String clientId;

    @Value("${github.clientSecret}")
    private String clientSecret;

    private final WebClient webClient = WebClient.create();

    public GithubUserDto loginWithGithub(String code) {

        Map tokenResponse = webClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header("Accept", "application/json")
                .bodyValue(Map.of(
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "code", code
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String accessToken = (String) tokenResponse.get("access_token");

        Map user = webClient.get()
                .uri("https://api.github.com/user")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        var emails = webClient.get()
                .uri("https://api.github.com/user/emails")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(List.class)
                .block();

        String email = null;
        if (emails != null && !emails.isEmpty()) {
            Map e = (Map) emails.get(0);
            email = (String) e.get("email");
        }

        return new GithubUserDto(
                (String) user.get("name"),
                email,
                (String) user.get("avatar_url")
        );
    }





}
