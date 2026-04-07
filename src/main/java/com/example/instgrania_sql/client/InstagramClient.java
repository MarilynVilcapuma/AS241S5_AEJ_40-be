package com.example.instgrania_sql.client;

import com.example.instgrania_sql.dto.InstagramPostDto;
import com.example.instgrania_sql.dto.InstagramProfileApiResponse;
import com.example.instgrania_sql.dto.InstagramProfileDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InstagramClient {

    private final WebClient instagramWebClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<InstagramProfileDto> getProfile(String username) {
        return instagramWebClient.post()
                .uri("/api/instagram/profile")
                .bodyValue(Map.of("username", username))
                .retrieve()
                .bodyToMono(InstagramProfileApiResponse.class)
                .map(InstagramProfileApiResponse::getResult);
    }

    public Mono<InstagramPostDto> getPostLinks(String postUrl) {
        String cleanUrl = cleanInstagramUrl(postUrl);
        return instagramWebClient.post()
                .uri("/api/instagram/links")
                .bodyValue(Map.of("url", cleanUrl))
                .retrieve()
                .bodyToMono(String.class)
                .map(raw -> {
                    try {
                        List<InstagramPostDto> list = objectMapper.readValue(raw,
                                objectMapper.getTypeFactory().constructCollectionType(
                                        List.class, InstagramPostDto.class));
                        return list.isEmpty() ? new InstagramPostDto() : list.get(0);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing links: " + e.getMessage(), e);
                    }
                });
    }

    public Mono<String> getPostLinksRaw(String postUrl) {
        String cleanUrl = cleanInstagramUrl(postUrl);
        return instagramWebClient.post()
                .uri("/api/instagram/links")
                .bodyValue(Map.of("url", cleanUrl))
                .retrieve()
                .bodyToMono(String.class);
    }

    private String cleanInstagramUrl(String postUrl) {
        String clean = postUrl.trim().replaceAll("^\"|\"$", "").split("\\?")[0];
        return clean.endsWith("/") ? clean : clean + "/";
    }
}
