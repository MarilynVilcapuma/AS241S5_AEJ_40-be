package com.example.detectoria_nosql.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "detections")
public class DetectionDocument {

    @Id
    private String id;

    private String inputText;
    private String verdict;
    private Double humanScore;
    private Double aiScore;
    private Integer totalWords;
    private String lang;
    private Boolean active = true;
    private LocalDateTime createdAt;

    public DetectionDocument(String inputText, String verdict, Double humanScore,
                             Double aiScore, Integer totalWords, String lang) {
        this.inputText = inputText;
        this.verdict = verdict;
        this.humanScore = humanScore;
        this.aiScore = aiScore;
        this.totalWords = totalWords;
        this.lang = lang;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }
}
