package com.fitgroup.backend.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitgroup.backend.challenge.entity.ChallengeParticipant;
import org.springframework.data.jpa.repository.Query;
import com.fitgroup.backend.challenge.entity.Challenge;
import java.util.List;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    @Query("""
    SELECT cp, c
    FROM ChallengeParticipant cp
    JOIN Challenge c ON cp.challengeId = c.id
    WHERE cp.userId = :userId AND c.isDeleted = false
    """)
    List<Object[]> findAllByUserWithChallenges(Long userId);

    boolean existsByChallengeIdAndUserId(Long challengeId, Long userId);

    @Query("""
    SELECT cp, u.fullName
    FROM ChallengeParticipant cp
    JOIN User u ON cp.userId = u.id
    WHERE cp.challengeId = :challengeId
    ORDER BY cp.totalPoints DESC, cp.maxStreak DESC, cp.joinedAt ASC
    """)
    List<Object[]> findLeaderboard(Long challengeId);

    List<ChallengeParticipant> findByChallengeId(Long challengeId);

    List<ChallengeParticipant> findByChallengeIdOrderByTotalPointsDesc(Long challengeId);
}

