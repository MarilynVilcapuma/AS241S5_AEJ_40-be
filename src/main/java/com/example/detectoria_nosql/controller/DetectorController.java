package com.example.detectoria_nosql.controller;

import com.example.detectoria_nosql.dto.DetectRequest;
import com.example.detectoria_nosql.dto.DetectionResultResponse;
import com.example.detectoria_nosql.model.DetectionDocument;
import com.example.detectoria_nosql.service.RapidApiDetectorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/detection")
public class DetectorController {

    private final RapidApiDetectorService detectorService;

    public DetectorController(RapidApiDetectorService detectorService) {
        this.detectorService = detectorService;
    }

    @GetMapping
    public Flux<DetectionDocument> getAllDetections() {
        return detectorService.getAllDetections();
    }

    @PostMapping
    public Mono<ResponseEntity<DetectionResultResponse>> detect(@RequestBody DetectRequest request) {
        return detectorService.detectText(request)
                .map(ResponseEntity::ok);
    }
}
