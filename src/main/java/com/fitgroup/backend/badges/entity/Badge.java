package com.fitgroup.backend.badges.entity;

import com.fitgroup.backend.badges.enums.BadgeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String code; // e.g., FIRST_CHECKIN, STREAK_3

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconUrl;

    @Enumerated(EnumType.STRING)
    private BadgeType badgeType = BadgeType.SYSTEM;

    private Long challengeId;

    private LocalDateTime createdAt;
}