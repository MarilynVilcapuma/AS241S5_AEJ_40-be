package com.example.detectoria_nosql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResultResponse {

    private String verdict;       // "HUMAN", "AI" o "MIXED"
    private Double humanScore;    // probabilidad de ser humano (0.0 a 1.0)
    private Double aiScore;       // probabilidad de ser IA (0.0 a 1.0)
    private Integer totalWords;
    private String lang;
}
