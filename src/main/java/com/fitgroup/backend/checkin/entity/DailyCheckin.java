package com.fitgroup.backend.checkin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_checkins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long challengeId;

    private Long userId;

    private LocalDate checkinDate;

    private Integer value;           // steps, minutes, sessions, etc.

    private Integer pointsEarned;    // computed based on value + goal

    private LocalDateTime createdAt;
}