package com.example.instgrania_sql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramProfileApiResponse {
    private InstagramProfileDto result;
}
