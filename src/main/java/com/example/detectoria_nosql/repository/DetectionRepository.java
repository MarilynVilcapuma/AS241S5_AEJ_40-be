package com.example.detectoria_nosql.repository;

import com.example.detectoria_nosql.model.DetectionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetectionRepository extends ReactiveMongoRepository<DetectionDocument, String> {
}
