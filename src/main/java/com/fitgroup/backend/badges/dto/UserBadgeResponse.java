package com.fitgroup.backend.badges.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserBadgeResponse {

    private Long badgeId;
    private String name;
    private String code;
    private String description;
    private String iconUrl;
    private String badgeType;

    private LocalDateTime awardedAt;
    private Long sourceChallengeId;  // optional → useful for FINISHER badge
}
