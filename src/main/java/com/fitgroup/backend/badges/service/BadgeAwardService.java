package com.fitgroup.backend.badges.service;

import com.fitgroup.backend.badges.dto.UserBadgeResponse;
import com.fitgroup.backend.badges.entity.Badge;
import com.fitgroup.backend.badges.entity.UserBadge;
import com.fitgroup.backend.badges.repository.BadgeRepository;
import com.fitgroup.backend.badges.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeAwardService {

    private final BadgeRepository badgeRepo;
    private final UserBadgeRepository userBadgeRepo;

    // Award badge safely (no duplicates)
    public void awardBadge(Long userId, String badgeCode, Long sourceChallengeId) {

        Badge badge = badgeRepo.findByCode(badgeCode)
                .orElseThrow(() -> new RuntimeException("Badge not found: " + badgeCode));

        boolean alreadyEarned = userBadgeRepo.existsByUserIdAndBadgeId(userId, badge.getId());
        if (alreadyEarned) return;

        UserBadge userBadge = UserBadge.builder()
                .userId(userId)
                .badgeId(badge.getId())
                .sourceChallengeId(sourceChallengeId)
                .awardedAt(LocalDateTime.now())
                .build();

        userBadgeRepo.save(userBadge);
    }

    public List<UserBadgeResponse> getUserBadgeDetails(Long userId) {

        return userBadgeRepo.findByUserId(userId).stream().map(ub -> {

            Badge badge = badgeRepo.findById(ub.getBadgeId())
                    .orElseThrow(() -> new RuntimeException("Badge not found"));

            return UserBadgeResponse.builder()
                    .badgeId(badge.getId())
                    .name(badge.getName())
                    .code(badge.getCode())
                    .description(badge.getDescription())
                    .iconUrl(badge.getIconUrl())
                    .badgeType(badge.getBadgeType().name())
                    .awardedAt(ub.getAwardedAt())
                    .sourceChallengeId(ub.getSourceChallengeId())
                    .build();

        }).toList();
    }

    // All badges earned by user
    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepo.findByUserId(userId);
    }

    // All badge definitions in system
    public List<Badge> getAllBadges() {
        return badgeRepo.findAll();
    }
}