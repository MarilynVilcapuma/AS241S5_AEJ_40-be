package com.example.instgrania_sql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramProfileDto {

    private String id;
    private String username;

    @JsonProperty("full_name")
    private String fullName;

    private String biography;

    @JsonProperty("profile_pic_url")
    private String profilePicUrl;

    @JsonProperty("is_private")
    private Boolean isPrivate;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("is_business_account")
    private Boolean isBusiness;

    @JsonProperty("business_category_name")
    private String category;

    @JsonProperty("external_url")
    private String externalUrl;

    @JsonProperty("edge_followed_by")
    private CountWrapper edgeFollowedBy;

    @JsonProperty("edge_follow")
    private CountWrapper edgeFollow;

    @JsonProperty("edge_owner_to_timeline_media")
    private CountWrapper edgeOwnerToTimelineMedia;

    public Long getFollowersCount() {
        return edgeFollowedBy != null ? edgeFollowedBy.getCount().longValue() : null;
    }

    public Long getFollowingCount() {
        return edgeFollow != null ? edgeFollow.getCount().longValue() : null;
    }

    public Integer getPostsCount() {
        return edgeOwnerToTimelineMedia != null ? edgeOwnerToTimelineMedia.getCount() : null;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountWrapper {
        private Integer count;
    }
}
