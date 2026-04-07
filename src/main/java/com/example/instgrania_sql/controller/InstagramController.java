package com.example.instgrania_sql.controller;

import com.example.instgrania_sql.dto.DownloadRequest;
import com.example.instgrania_sql.dto.DownloadResponse;
import com.example.instgrania_sql.model.PipelineExecution;
import com.example.instgrania_sql.model.Post;
import com.example.instgrania_sql.model.Profile;
import com.example.instgrania_sql.service.InstagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/instagram")
@RequiredArgsConstructor
public class InstagramController {

    private final InstagramService instagramService;

    @PostMapping("/pipeline/{username}")
    public Mono<PipelineExecution> runPipeline(
            @PathVariable String username,
            @RequestBody List<String> postUrls) {
        return instagramService.runPipeline(username, postUrls);
    }

    @PostMapping("/profile/{username}")
    public Mono<Profile> fetchProfile(@PathVariable String username) {
        return instagramService.fetchAndSaveProfile(username);
    }

    @GetMapping("/profile/{username}")
    public Mono<Profile> getProfile(@PathVariable String username) {
        return instagramService.getProfile(username);
    }

    @GetMapping("/posts/{username}")
    public Flux<Post> getPosts(@PathVariable String username) {
        return instagramService.getPostsByUsername(username);
    }

    @GetMapping("/executions/{username}")
    public Flux<PipelineExecution> getExecutions(@PathVariable String username) {
        return instagramService.getExecutions(username);
    }

    @PostMapping("/download")
    public Mono<DownloadResponse> getDownloadInfo(@RequestBody DownloadRequest request) {
        return instagramService.getDownloadInfo(request.getUrl(), request.getUsername());
    }

    @PostMapping("/links/raw")
    public Mono<String> getLinksRaw(@RequestBody DownloadRequest request) {
        return instagramService.getLinksRaw(request.getUrl());
    }
}
