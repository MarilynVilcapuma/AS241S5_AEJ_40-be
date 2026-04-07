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

    private String instagramId;
    private String username;
    private String sourceUrl;
    private String shortCode;
    private String type;               // IMAGE, VIDEO, CAROUSEL, REEL
    private String caption;
    private String[] hashtags;
    private Integer hashtagCount;
    private String[] mentions;
    private Long likesCount;
    private Long commentsCount;
    private Long viewsCount;
    private Double engagementRate;
    private Integer mediaCount;
    private Integer durationSeconds;
    private String[] downloadUrls;
    private LocalDateTime postedAt;
    private Integer postedHour;
    private Integer postedDayOfWeek;
    private LocalDateTime savedAt;
}
