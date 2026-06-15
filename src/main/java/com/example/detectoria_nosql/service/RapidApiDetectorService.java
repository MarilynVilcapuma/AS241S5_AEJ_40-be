package com.example.detectoria_nosql.service;

import com.example.detectoria_nosql.dto.DetectRequest;
import com.example.detectoria_nosql.dto.DetectResponse;
import com.example.detectoria_nosql.dto.DetectionResultResponse;
import com.example.detectoria_nosql.dto.UpdateDetectionRequest;
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

    public Mono<DetectionDocument> getById(String id) {
        return findByIdOrError(id);
    }

    public Mono<DetectionDocument> updateDetection(String id, UpdateDetectionRequest request) {
        return findByIdOrError(id)
                .flatMap(doc -> {
                    if (request.getVerdict()    != null) doc.setVerdict(request.getVerdict());
                    if (request.getHumanScore() != null) doc.setHumanScore(request.getHumanScore());
                    if (request.getAiScore()    != null) doc.setAiScore(request.getAiScore());
                    if (request.getTotalWords() != null) doc.setTotalWords(request.getTotalWords());
                    if (request.getLang()       != null) doc.setLang(request.getLang());
                    return repository.save(doc);
                });
    }

    public Mono<DetectionDocument> deactivate(String id) {
        return findByIdOrError(id)
                .flatMap(doc -> {
                    doc.setActive(false);
                    return repository.save(doc);
                });
    }

    public Mono<DetectionDocument> restore(String id) {
        return findByIdOrError(id)
                .flatMap(doc -> {
                    doc.setActive(true);
                    return repository.save(doc);
                });
    }

    public Mono<DetectionResultResponse> detectText(DetectRequest request) {
        // VULNERABILIDAD DE PRUEBA: Log Injection con input del usuario
        log.info("Procesando texto del usuario: " + request.getText());
        return webClient.post()
                .uri(endpointPath)
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", host)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DetectResponse.class)
                .flatMap(response -> {
                    DetectResponse.Summary summary = response.getSummary();
                    String verdict = calculateVerdict(summary);
                    Double humanScore = summary != null ? summary.getHuman() : null;
                    Double aiScore = summary != null ? summary.getAi() : response.getAiScore();

                    DetectionDocument doc = new DetectionDocument(
                            request.getText(), verdict, humanScore, aiScore,
                            response.getTotalNumWords(), response.getLang()
                    );

                    return repository.save(doc)
                            .doOnSuccess(saved -> log.info("Detection saved id={} verdict={}", saved.getId(), saved.getVerdict()))
                            .doOnError(ex -> log.error("Failed to save detection: {}", ex.getMessage()))
                            .thenReturn(new DetectionResultResponse(verdict, humanScore, aiScore, response.getTotalNumWords(), response.getLang()));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("RapidAPI error: status={}", ex.getStatusCode());
                    return Mono.error(new IllegalStateException(
                            "RapidAPI request failed with status: " + ex.getStatusCode(), ex));
                });
    }

    private Mono<DetectionDocument> findByIdOrError(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Detection not found: " + id)));
    }

    private String calculateVerdict(DetectResponse.Summary summary) {
        if (summary == null) return "UNKNOWN";
        Double human = summary.getHuman();
        Double ai = summary.getAi();
        if (human != null && human >= 0.5) return "HUMAN";
        if (ai != null && ai >= 0.5) return "AI";
        return "MIXED";
    }
}
