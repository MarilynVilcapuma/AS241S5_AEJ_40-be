package com.example.instgrania_sql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramPostDto {

    private List<UrlItem> urls;
    private Meta meta;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UrlItem {
        private String url;
        private String name;
        private String extension;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private String title;
        private String sourceUrl;
        private String shortcode;
        private Long commentCount;
        private Long likeCount;
        private Long takenAt;
        private String pictureUrl;
        private String service;
    }

    public String getFirstDownloadUrl() {
        if (urls != null && !urls.isEmpty()) return urls.get(0).getUrl();
        return null;
    }

    public String getExtension() {
        if (urls != null && !urls.isEmpty()) return urls.get(0).getExtension();
        return null;
    }

    public String getType() {
        if (urls == null || urls.isEmpty()) return null;
        String ext = urls.get(0).getExtension();
        if (ext == null) return null;
        return switch (ext.toLowerCase()) {
            case "mp4" -> "VIDEO";
            case "jpg", "jpeg", "png", "webp" -> "IMAGE";
            default -> ext.toUpperCase();
        };
    }
}
