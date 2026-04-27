package com.example.detectoria_nosql.service;

import com.example.detectoria_nosql.dto.DetectRequest;
import com.example.detectoria_nosql.dto.DetectResponse;
import com.example.detectoria_nosql.dto.DetectionResultResponse;
import com.example.detectoria_nosql.model.DetectionDocument;
import com.example.detectoria_nosql.repository.DetectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RapidApiDetectorService {

    private static final Logger log = LoggerFactory.getLogger(RapidApiDetectorService.class);

    private final WebClient webClient;
    private final DetectionRepository repository;
    private final String host;
    private final String apiKey;
    private final String endpointPath;

    public RapidApiDetectorService(WebClient rapidApiWebClient,
                                   DetectionRepository repository,
                                   @Value("${rapidapi.host}") String host,
                                   @Value("${rapidapi.key}") String apiKey,
                                   @Value("${rapidapi.path}") String endpointPath) {
        this.webClient = rapidApiWebClient;
        this.repository = repository;
        this.host = host;
        this.apiKey = apiKey;
        this.endpointPath = endpointPath;
    }

    public Flux<DetectionDocument> getAllDetections() {
        return repository.findAll();
    }

    public Mono<DetectionResultResponse> detectText(DetectRequest request) {
        return webClient.post()
                .uri(endpointPath)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", host)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DetectResponse.class)
                .flatMap(response -> {
                    String verdict = response.getVerdict();
                    Double humanScore = response.getSummary() != null ? response.getSummary().getHuman() : null;
                    Double aiScore = response.getAiScore();

                    DetectionDocument doc = new DetectionDocument(
                            request.getText(),
                            verdict,
                            humanScore,
                            aiScore,
                            response.getTotalNumWords(),
                            response.getLang()
                    );

                    return repository.save(doc)
                            .doOnSuccess(saved -> log.info("Detection saved with id: {} | verdict: {}", saved.getId(), saved.getVerdict()))
                            .doOnError(ex -> log.error("Failed to save to MongoDB: {}", ex.getMessage()))
                            .thenReturn(new DetectionResultResponse(verdict, humanScore, aiScore, response.getTotalNumWords(), response.getLang()));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("RapidAPI request failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new IllegalStateException(
                            "RapidAPI request failed: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex));
                });
    }
}
