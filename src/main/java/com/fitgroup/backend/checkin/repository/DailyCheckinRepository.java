package com.fitgroup.backend.checkin.repository;


import com.fitgroup.backend.checkin.entity.DailyCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCheckinRepository extends JpaRepository<DailyCheckin, Long> {

    Optional<DailyCheckin> findByChallengeIdAndUserIdAndCheckinDate(
            Long challengeId,
            Long userId,
            LocalDate checkinDate
    );
    long countByUserId(Long userId);

    @Query("""
    SELECT COALESCE(SUM(c.value), 0)
    FROM DailyCheckin c
    WHERE c.challengeId = :challengeId AND c.userId = :userId
    """)
    Integer getTotalProgress(Long challengeId, Long userId);
}