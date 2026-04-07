package com.example.instgrania_sql.repository;

import com.example.instgrania_sql.model.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PostRepository extends ReactiveCrudRepository<Post, Long> {

    Flux<Post> findByUsername(String username);

    Mono<Post> findBySourceUrl(String sourceUrl);
}
