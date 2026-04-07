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
@Table("profiles")
public class Profile {

    @Id
    private Long id;

    private String username;
    private String fullName;
    private String bio;
    private String profilePicUrl;
    private Boolean isVerified;
    private Boolean isBusiness;
    private String category;
    private String externalUrl;
    private Long followersCount;
    private Long followingCount;
    private Integer postsCount;
    private Double avgEngagementRate;
    private LocalDateTime lastFetchedAt;
    private LocalDateTime savedAt;
}
