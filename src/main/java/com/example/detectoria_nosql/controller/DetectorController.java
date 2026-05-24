package com.example.detectoria_nosql.controller;

import com.example.detectoria_nosql.dto.DetectRequest;
import com.example.detectoria_nosql.dto.DetectionResultResponse;
import com.example.detectoria_nosql.dto.UpdateDetectionRequest;
import com.example.detectoria_nosql.model.DetectionDocument;
import com.example.detectoria_nosql.service.RapidApiDetectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/detection")
@RequiredArgsConstructor
public class DetectorController {

    private final RapidApiDetectorService detectorService;

    @GetMapping
    public Flux<DetectionDocument> getAllDetections() {
        return detectorService.getAllDetections();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DetectionDocument>> getById(@PathVariable String id) {
        return okOrNotFound(detectorService.getById(id));
    }

    @PostMapping
    public Mono<ResponseEntity<DetectionResultResponse>> detect(@RequestBody DetectRequest request) {
        return detectorService.detectText(request)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<DetectionDocument>> update(
            @PathVariable String id,
            @RequestBody UpdateDetectionRequest request) {
        return okOrNotFound(detectorService.updateDetection(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<ResponseEntity<DetectionDocument>> deactivate(@PathVariable String id) {
        return okOrNotFound(detectorService.deactivate(id));
    }

    @PatchMapping("/{id}/restore")
    public Mono<ResponseEntity<DetectionDocument>> restore(@PathVariable String id) {
        return okOrNotFound(detectorService.restore(id));
    }

    private <T> Mono<ResponseEntity<T>> okOrNotFound(Mono<T> source) {
        return source
                .map(ResponseEntity::ok)
                .onErrorReturn(IllegalArgumentException.class, ResponseEntity.notFound().build());
    }
}
