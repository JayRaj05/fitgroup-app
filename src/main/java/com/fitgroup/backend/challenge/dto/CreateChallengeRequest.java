package com.fitgroup.backend.challenge.dto;

import com.fitgroup.backend.challenge.enums.ChallengeType;
import com.fitgroup.backend.challenge.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChallengeRequest {

    public String name;
    public String description;

    public ChallengeType type;      // PERSONAL, GROUP, EVENT
    public Visibility visibility;   // PUBLIC, PRIVATE

    public String goalType;         // STEPS, DURATION, etc.
    public Double targetValue;

    public LocalDate startDate;
    public LocalDate endDate;
}
