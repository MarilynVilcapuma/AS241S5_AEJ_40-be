package com.example.instgrania_sql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("posts")
public class Post {

    @Id
    private Long id;

    private String username;
    private String sourceUrl;
    private String mediaUrl;
    private String mediaType;
    private String caption;

    private Boolean active;
    private LocalDateTime savedAt;
}
