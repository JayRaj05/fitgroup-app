package com.fitgroup.backend.challenge.entity;

import com.fitgroup.backend.challenge.enums.ChallengeType;
import com.fitgroup.backend.challenge.enums.GoalType;
import com.fitgroup.backend.challenge.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ChallengeType type;   // PERSONAL, GROUP, EVENT

    private Long createdBy;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    private GoalType goalType;

    private Double targetValue;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive = true;

    private Integer participantCount;

    private Boolean isDeleted = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
