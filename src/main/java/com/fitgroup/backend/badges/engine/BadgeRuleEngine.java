package com.fitgroup.backend.badges.engine;

import com.fitgroup.backend.badges.service.BadgeAwardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BadgeRuleEngine {

    private final BadgeAwardService badgeAwardService;

    // RULE 1: First check-in badge
    public void onFirstCheckin(Long userId) {
        badgeAwardService.awardBadge(userId, "FIRST_CHECKIN", null);
    }

    // RULE 2: Streak badges
    public void onStreakUpdate(Long userId, int streak) {
        if (streak == 3) badgeAwardService.awardBadge(userId, "STREAK_3", null);
        else if (streak == 7) badgeAwardService.awardBadge(userId, "STREAK_7", null);
        else if (streak == 30) badgeAwardService.awardBadge(userId, "STREAK_30", null);
    }

    // RULE 3: Challenge creator badge
    public void onChallengeCreated(Long userId) {
        badgeAwardService.awardBadge(userId, "CREATOR", null);
    }

    // RULE 4: Challenge finisher badge
    public void onChallengeFinished(Long userId, Long challengeId) {
        badgeAwardService.awardBadge(userId, "FINISHER", challengeId);
    }

    // RULE 5: Top performer
    public void awardTopPerformer(Long userId, Long challengeId) {
        badgeAwardService.awardBadge(userId, "TOP_PERFORMER", challengeId);
    }
}
