package com.fitgroup.backend.challenge.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyProgressResponse {

    private LocalDate date;
    private Integer totalValue;
    private Integer totalPoints;
}
