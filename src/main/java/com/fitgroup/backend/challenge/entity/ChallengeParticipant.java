package com.fitgroup.backend.challenge.entity;

import com.fitgroup.backend.challenge.enums.ParticipantRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long challengeId;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ParticipantRole role;  // OWNER, PARTICIPANT

    private Integer totalPoints = 0;

    private Integer currentStreak = 0;

    private Integer maxStreak = 0;

    private LocalDateTime joinedAt;
}