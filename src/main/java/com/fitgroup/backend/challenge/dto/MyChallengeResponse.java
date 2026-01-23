package com.fitgroup.backend.challenge.dto;

import com.fitgroup.backend.challenge.enums.ChallengeType;
import com.fitgroup.backend.challenge.enums.GoalType;
import com.fitgroup.backend.challenge.enums.ParticipantRole;
import com.fitgroup.backend.challenge.enums.Visibility;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MyChallengeResponse {

    private Long challengeId;
    private String name;
    private String description;

    private ChallengeType type;
    private Visibility visibility;

    private GoalType goalType;
    private Double targetValue;

    private LocalDate startDate;
    private LocalDate endDate;

    private ParticipantRole role;
    private int totalPoints;
    private int currentStreak;
    private int maxStreak;
}