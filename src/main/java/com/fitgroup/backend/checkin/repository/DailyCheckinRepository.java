package com.fitgroup.backend.checkin.repository;


import com.fitgroup.backend.checkin.entity.DailyCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
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


    @Query("""
    SELECT
        d.checkinDate AS checkinDate,
        SUM(d.pointsEarned) AS totalPoints,
        SUM(d.value) AS totalValue
    FROM DailyCheckin d
    WHERE d.userId = :userId
    AND d.checkinDate >= :startDate
    GROUP BY d.checkinDate
    """)
    List<DailyCheckinAggregate> findCalendarData(
            Long userId,
            LocalDate startDate
    );

    @Query("""
    SELECT
        d.checkinDate,
    SUM(d.value),
    SUM(d.pointsEarned)
    FROM DailyCheckin d
    WHERE d.challengeId = :challengeId
    AND d.userId = :userId
    GROUP BY d.checkinDate
    ORDER BY d.checkinDate
    """)
    List<Object[]> getDailyProgress(Long challengeId, Long userId);

}