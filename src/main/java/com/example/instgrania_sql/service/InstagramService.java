package com.example.instgrania_sql.service;

import com.example.instgrania_sql.client.InstagramClient;
import com.example.instgrania_sql.dto.DownloadResponse;
import com.example.instgrania_sql.dto.InstagramPostDto;
import com.example.instgrania_sql.dto.InstagramProfileDto;
import com.example.instgrania_sql.model.PipelineExecution;
import com.example.instgrania_sql.model.Post;
import com.example.instgrania_sql.model.Profile;
import com.example.instgrania_sql.repository.PipelineExecutionRepository;
import com.example.instgrania_sql.repository.PostRepository;
import com.example.instgrania_sql.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class InstagramService {

    private final InstagramClient instagramClient;
    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final PipelineExecutionRepository pipelineExecutionRepository;

    // --- Pipeline completo: perfil + posts + media ---
    public Mono<PipelineExecution> runPipeline(String username, List<String> postUrls) {
        AtomicInteger fetched = new AtomicInteger(0);
        AtomicInteger saved = new AtomicInteger(0);
        LocalDateTime startedAt = LocalDateTime.now();

        Flux<Post> postFlux = fetchAndSaveProfile(username)
                .thenMany(Flux.fromIterable(postUrls))
                .flatMap(url -> instagramClient.getPostLinks(url)
                        .doOnNext(dto -> fetched.incrementAndGet())
                        .map(dto -> toPostEntity(dto, username, url))
                        .flatMap(post -> postRepository.save(post)
                                .doOnSuccess(p -> saved.incrementAndGet())));

        return postFlux
                .then(Mono.defer(() -> savePipelineExecution(username, "SUCCESS",
                        fetched.get(), saved.get(), null, startedAt)))
                .onErrorResume(ex -> savePipelineExecution(username, "FAILED",
                        fetched.get(), saved.get(), ex.getMessage(), startedAt));
    }

    // --- Perfil ---
    public Mono<Profile> fetchAndSaveProfile(String username) {
        return instagramClient.getProfile(username)
                .map(this::toProfileEntity)
                .flatMap(profileRepository::save);
    }

    public Mono<Profile> getProfile(String username) {
        return profileRepository.findByUsername(username);
    }

    // --- Posts guardados ---
    public Flux<Post> getPostsByUsername(String username) {
        return postRepository.findByUsername(username);
    }

    // --- Historial de ejecuciones ---
    public Flux<PipelineExecution> getExecutions(String username) {
        return pipelineExecutionRepository.findByUsernameOrderByExecutedAtDesc(username);
    }

    // --- Descarga con guardado condicional ---
    public Mono<DownloadResponse> getDownloadInfo(String postUrl, String username) {
        String cleanUrl = cleanUrl(postUrl);

        return postRepository.findBySourceUrl(cleanUrl)
                .flatMap(existingPost -> Mono.just(toDownloadResponseFromPost(existingPost)))
                .switchIfEmpty(
                    instagramClient.getPostLinks(cleanUrl)
                        .flatMap(dto -> ensureProfileExists(username)
                                .then(postRepository.save(toPostEntity(dto, username, cleanUrl)))
                                .map(saved -> toDownloadResponse(dto, cleanUrl))
                                .onErrorResume(ex -> Mono.just(toDownloadResponse(dto, cleanUrl))))
                );
    }

    // --- Temporal: debug ---
    public Mono<String> getLinksRaw(String postUrl) {
        return instagramClient.getPostLinksRaw(postUrl);
    }

    private Mono<Profile> ensureProfileExists(String username) {
        if (username == null || username.isBlank()) return Mono.empty();
        return profileRepository.findByUsername(username)
                .switchIfEmpty(
                    instagramClient.getProfile(username)
                            .map(this::toProfileEntity)
                            .flatMap(profileRepository::save)
                            .onErrorResume(ex -> {
                                // Si falla obtener el perfil, continua sin bloquearse
                                System.out.println("No se pudo obtener perfil de " + username + ": " + ex.getMessage());
                                return Mono.empty();
                            })
                );
    }

    // --- Mapeos ---
    private Profile toProfileEntity(InstagramProfileDto dto) {
        return Profile.builder()
                .username(dto.getUsername())
                .fullName(dto.getFullName())
                .bio(dto.getBiography())
                .profilePicUrl(dto.getProfilePicUrl())
                .isVerified(dto.getIsVerified())
                .isBusiness(dto.getIsBusiness())
                .category(dto.getCategory())
                .externalUrl(dto.getExternalUrl())
                .followersCount(dto.getFollowersCount())
                .followingCount(dto.getFollowingCount())
                .postsCount(dto.getPostsCount())
                .lastFetchedAt(LocalDateTime.now())
                .savedAt(LocalDateTime.now())
                .build();
    }

    private Post toPostEntity(InstagramPostDto dto, String username, String sourceUrl) {
        var meta = dto.getMeta();

        LocalDateTime postedAt = null;
        if (meta != null && meta.getTakenAt() != null) {
            postedAt = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(meta.getTakenAt()), ZoneId.systemDefault());
        }

        String[] downloadUrls = dto.getUrls() == null ? null :
                dto.getUrls().stream().map(InstagramPostDto.UrlItem::getUrl).toArray(String[]::new);

        return Post.builder()
                .username(username)
                .sourceUrl(sourceUrl)
                .shortCode(meta != null ? meta.getShortcode() : null)
                .type(dto.getType())
                .caption(meta != null ? meta.getTitle() : null)
                .likesCount(meta != null ? meta.getLikeCount() : null)
                .commentsCount(meta != null ? meta.getCommentCount() : null)
                .downloadUrls(downloadUrls)
                .postedAt(postedAt)
                .postedHour(postedAt != null ? postedAt.getHour() : null)
                .postedDayOfWeek(postedAt != null ? postedAt.getDayOfWeek().getValue() : null)
                .savedAt(LocalDateTime.now())
                .build();
    }

    private DownloadResponse toDownloadResponse(InstagramPostDto dto, String sourceUrl) {
        var meta = dto.getMeta();
        List<DownloadResponse.MediaItem> mediaItems = dto.getUrls() == null
                ? List.of()
                : dto.getUrls().stream()
                        .map(u -> DownloadResponse.MediaItem.builder()
                                .downloadUrl(u.getUrl())
                                .name(u.getName())
                                .extension(u.getExtension())
                                .build())
                        .toList();

        return DownloadResponse.builder()
                .sourceUrl(meta != null ? meta.getSourceUrl() : sourceUrl)
                .shortcode(meta != null ? meta.getShortcode() : null)
                .type(dto.getType())
                .thumbnailUrl(meta != null ? meta.getPictureUrl() : null)
                .likeCount(meta != null ? meta.getLikeCount() : null)
                .commentCount(meta != null ? meta.getCommentCount() : null)
                .title(meta != null ? meta.getTitle() : null)
                .mediaItems(mediaItems)
                .build();
    }

    private DownloadResponse toDownloadResponseFromPost(Post post) {
        List<DownloadResponse.MediaItem> mediaItems = post.getDownloadUrls() == null
                ? List.of()
                : java.util.Arrays.stream(post.getDownloadUrls())
                        .map(url -> DownloadResponse.MediaItem.builder()
                                .downloadUrl(url)
                                .extension(post.getType() != null &&
                                        post.getType().equals("VIDEO") ? "mp4" : "jpg")
                                .build())
                        .toList();

        return DownloadResponse.builder()
                .sourceUrl(post.getSourceUrl())
                .shortcode(post.getShortCode())
                .type(post.getType())
                .likeCount(post.getLikesCount())
                .commentCount(post.getCommentsCount())
                .title(post.getCaption())
                .mediaItems(mediaItems)
                .build();
    }

    private Mono<PipelineExecution> savePipelineExecution(String username, String status,
                                                           int fetched, int saved,
                                                           String errorMessage,
                                                           LocalDateTime executedAt) {
        return pipelineExecutionRepository.save(PipelineExecution.builder()
                .username(username)
                .status(status)
                .postsFetched(fetched)
                .postsSaved(saved)
                .errorMessage(errorMessage)
                .executedAt(executedAt)
                .build());
    }

    private String cleanUrl(String url) {
        String clean = url.trim().split("\\?")[0];
        return clean.endsWith("/") ? clean : clean + "/";
    }
}
