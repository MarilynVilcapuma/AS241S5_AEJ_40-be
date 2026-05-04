package com.example.instgrania_sql.service;

import com.example.instgrania_sql.client.InstagramClient;
import com.example.instgrania_sql.dto.InstagramProfileDto;
import com.example.instgrania_sql.model.Post;
import com.example.instgrania_sql.model.Profile;
import com.example.instgrania_sql.repository.PostRepository;
import com.example.instgrania_sql.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InstagramService {

    private final InstagramClient instagramClient;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;

    // ── PROFILE ──────────────────────────────────────────────────────────────

    public Mono<Profile> createProfile(String username) {
        return instagramClient.getProfile(username)
                .map(this::toProfileEntity)
                .flatMap(profileRepository::save);
    }

    public Flux<Profile> listProfiles(Boolean active) {
        if (active != null) return profileRepository.findByActive(active);
        return profileRepository.findAll();
    }

    public Mono<Profile> getProfile(String username) {
        return profileRepository.findByUsername(username);
    }

    public Mono<Profile> updateProfile(String username) {
        return profileRepository.findByUsername(username)
                .flatMap(existing -> instagramClient.getProfile(username)
                        .map(dto -> applyProfileUpdates(existing, dto))
                        .flatMap(profileRepository::save));
    }

    public Mono<Profile> deactivateProfile(Long id) {
        return profileRepository.findById(id)
                .flatMap(profile -> {
                    profile.setActive(false);
                    return profileRepository.save(profile);
                });
    }

    public Mono<Profile> activateProfile(Long id) {
        return profileRepository.findById(id)
                .flatMap(profile -> {
                    profile.setActive(true);
                    return profileRepository.save(profile);
                });
    }

    // ── MEDIA ─────────────────────────────────────────────────────────────────

    public Mono<Post> createMedia(String username, String postUrl) {
        String cleanUrl = cleanUrl(postUrl);
        return postRepository.findBySourceUrl(cleanUrl)
                .switchIfEmpty(
                    instagramClient.getPostLinks(cleanUrl)
                            .map(dto -> Post.builder()
                                    .username(username)
                                    .sourceUrl(cleanUrl)
                                    .mediaUrl(dto.getFirstDownloadUrl())
                                    .mediaType(resolveType(dto.getExtension()))
                                    .caption(dto.getMeta() != null ? dto.getMeta().getTitle() : null)
                                    .active(true)
                                    .savedAt(LocalDateTime.now())
                                    .build())
                            .flatMap(postRepository::save)
                );
    }

    public Flux<Post> listAllMedia(Boolean active) {
        if (active != null) return postRepository.findByActive(active);
        return postRepository.findAll();
    }

    public Flux<Post> listMedia(String username, Boolean active) {
        if (active != null) return postRepository.findByUsernameAndActive(username, active);
        return postRepository.findByUsername(username);
    }

    public Mono<Post> deactivateMedia(Long id) {
        return postRepository.findById(id)
                .flatMap(post -> {
                    post.setActive(false);
                    return postRepository.save(post);
                });
    }

    public Mono<Post> activateMedia(Long id) {
        return postRepository.findById(id)
                .flatMap(post -> {
                    post.setActive(true);
                    return postRepository.save(post);
                });
    }

    // ── Mapeos privados ───────────────────────────────────────────────────────

    private Profile toProfileEntity(InstagramProfileDto dto) {
        return Profile.builder()
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .bio(dto.getBiography())
                .profilePicUrl(dto.getProfilePicUrl())
                .followersCount(dto.getFollowersCount())
                .followingCount(dto.getFollowingCount())
                .postsCount(dto.getPostsCount())
                .isVerified(dto.getIsVerified())
                .active(true)
                .savedAt(LocalDateTime.now())
                .build();
    }

    private Profile applyProfileUpdates(Profile existing, InstagramProfileDto dto) {
        existing.setFullName(dto.getFullName());
        existing.setBio(dto.getBiography());
        existing.setProfilePicUrl(dto.getProfilePicUrl());
        existing.setFollowersCount(dto.getFollowersCount());
        existing.setFollowingCount(dto.getFollowingCount());
        existing.setPostsCount(dto.getPostsCount());
        existing.setIsVerified(dto.getIsVerified());
        return existing;
    }

    private String resolveType(String extension) {
        if (extension == null) return null;
        return switch (extension.toLowerCase()) {
            case "mp4" -> "VIDEO";
            case "jpg", "jpeg", "png", "webp" -> "IMAGE";
            default -> extension.toUpperCase();
        };
    }

    private String cleanUrl(String url) {
        String clean = url.trim().split("\\?")[0];
        return clean.endsWith("/") ? clean : clean + "/";
    }
}
