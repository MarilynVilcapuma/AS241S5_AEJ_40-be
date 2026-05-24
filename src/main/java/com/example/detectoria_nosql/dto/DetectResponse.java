package com.example.detectoria_nosql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectResponse {

    @JsonProperty("ai_score")
    private Double aiScore;

    @JsonProperty("summary")
    private Summary summary;

    @JsonProperty("total_num_words")
    private Integer totalNumWords;

    @JsonProperty("lang")
    private String lang;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary {
        @JsonProperty("human")
        private Double human;

        @JsonProperty("ai")
        private Double ai;

        @JsonProperty("mixed")
        private Double mixed;
    }

}
