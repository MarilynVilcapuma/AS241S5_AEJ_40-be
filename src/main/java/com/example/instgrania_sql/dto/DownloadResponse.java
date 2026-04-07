package com.example.instgrania_sql.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DownloadResponse {
    private String sourceUrl;
    private String shortcode;
    private String type;
    private String thumbnailUrl;
    private Long likeCount;
    private Long commentCount;
    private String title;
    private List<MediaItem> mediaItems;

    @Data
    @Builder
    public static class MediaItem {
        private String downloadUrl;
        private String name;
        private String extension;
    }
}
