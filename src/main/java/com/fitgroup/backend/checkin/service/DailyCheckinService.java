package com.fitgroup.backend.checkin.service;

import com.fitgroup.backend.badges.engine.BadgeRuleEngine;
import com.fitgroup.backend.checkin.dto.DailyCheckinRequest;
import com.fitgroup.backend.checkin.entity.DailyCheckin;
import com.fitgroup.backend.checkin.repository.DailyCheckinRepository;
import com.fitgroup.backend.challenge.entity.Challenge;
import com.fitgroup.backend.challenge.entity.ChallengeParticipant;
import com.fitgroup.backend.challenge.enums.GoalType;
import com.fitgroup.backend.challenge.repository.ChallengeParticipantRepository;
import com.fitgroup.backend.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DailyCheckinService {

    private final DailyCheckinRepository checkinRepo;
    private final ChallengeRepository challengeRepo;
    private final ChallengeParticipantRepository participantRepo;
    private final BadgeRuleEngine badgeRuleEngine;


    public DailyCheckin checkIn(Long userId, DailyCheckinRequest req) {

        Long challengeId = req.challengeId;
        Integer value = req.value;
        LocalDate today = LocalDate.now();

        // 1. Challenge exists
        Challenge challenge = challengeRepo.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // 2. User is participant
        ChallengeParticipant participant = participantRepo
                .findAll()
                .stream()
                .filter(p -> p.getUserId().equals(userId) && p.getChallengeId().equals(challengeId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not part of challenge"));

        // 3. Already checked in today?
        if (checkinRepo.findByChallengeIdAndUserIdAndCheckinDate(challengeId, userId, today).isPresent()) {
            throw new RuntimeException("You already checked in today");
        }

        // 4. Streak logic
        LocalDate yesterday = today.minusDays(1);
        boolean hasYesterdayCheckin = checkinRepo
                .findByChallengeIdAndUserIdAndCheckinDate(challengeId, userId, yesterday)
                .isPresent();

        int newStreak = hasYesterdayCheckin ? participant.getCurrentStreak() + 1 : 1;

        // 5. Point calculation (based on GoalType)
        int basePoints = calculatePoints(challenge.getGoalType(), value, challenge.getTargetValue());

        // streak bonus
        int finalPoints = basePoints + newStreak;

        // 6. Update participant stats
        participant.setCurrentStreak(newStreak);
        participant.setTotalPoints(participant.getTotalPoints() + finalPoints);
        participant.setMaxStreak(Math.max(participant.getMaxStreak(), newStreak));
        participantRepo.save(participant);

        // 7. Save check-in
        DailyCheckin checkin = DailyCheckin.builder()
                .challengeId(challengeId)
                .userId(userId)
                .checkinDate(today)
                .value(value)
                .pointsEarned(finalPoints)
                .createdAt(LocalDateTime.now())
                .build();

        // 8. FIRST CHECK-IN BADGE
        long totalCheckins = checkinRepo.countByUserId(userId);
        if (totalCheckins == 0) {
            badgeRuleEngine.onFirstCheckin(userId);
        }

        // 9. STREAK BADGES
        badgeRuleEngine.onStreakUpdate(userId, newStreak);

        // Save last
        return checkinRepo.save(checkin);
    }



    private int calculatePoints(GoalType goalType, int value, Double targetValue) {

        switch (goalType) {

            case STEPS:
                int pts = value / 1000;
                if (value >= targetValue) pts += 5;
                return pts;

            case DURATION:
                int p2 = value / 10;
                if (value > targetValue) {
                    p2 += (value - targetValue) / 20;
                }
                return p2;

            case SESSIONS:
                return value >= 1 ? 10 : 0;

            case CUSTOM:
                int p3 = value;
                if (value >= targetValue) p3 += 10;
                return p3;

            default:
                return 0;
        }
    }
}
