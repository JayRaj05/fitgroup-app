package com.fitgroup.backend.challenge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntry {

    private Long userId;
    private String fullName;

    private int totalPoints;
    private int currentStreak;
    private int maxStreak;

    private int rank;
}
