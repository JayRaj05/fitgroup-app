package com.fitgroup.backend.checkin.repository;

import java.time.LocalDate;

public interface DailyCheckinAggregate {
    LocalDate getCheckinDate();
    Integer getTotalPoints();
    Integer getTotalValue();
}
