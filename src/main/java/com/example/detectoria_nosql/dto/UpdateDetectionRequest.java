package com.example.detectoria_nosql.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDetectionRequest {

    private String verdict;
    private Double humanScore;
    private Double aiScore;
    private Integer totalWords;
    private String lang;
}
