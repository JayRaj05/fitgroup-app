package com.fitgroup.backend.checkin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CheckinCalendarDayResponse {

    private LocalDate date;
    private boolean checkedIn;
    private int totalPoints;
    private int totalValue;
}