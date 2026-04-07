package com.example.instgrania_sql.repository;

import com.example.instgrania_sql.model.PipelineExecution;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PipelineExecutionRepository extends ReactiveCrudRepository<PipelineExecution, Long> {

    Flux<PipelineExecution> findByUsernameOrderByExecutedAtDesc(String username);
}
