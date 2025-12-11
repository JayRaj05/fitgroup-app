package com.fitgroup.backend.badges.config;

import com.fitgroup.backend.badges.entity.Badge;
import com.fitgroup.backend.badges.enums.BadgeType;
import com.fitgroup.backend.badges.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BadgeInitializer {

    private final BadgeRepository badgeRepository;

    @PostConstruct
    public void loadSystemBadges() {

        List<Badge> systemBadges = List.of(
                create("First Check-in", "FIRST_CHECKIN",
                        "Awarded for completing your very first check-in.", "icon_first.png"),

                create("3-Day Streak", "STREAK_3",
                        "Awarded for maintaining a 3-day check-in streak.", "icon_3.png"),

                create("7-Day Streak", "STREAK_7",
                        "Awarded for maintaining a 7-day check-in streak.", "icon_7.png"),

                create("30-Day Streak", "STREAK_30",
                        "A dedication award for a 30-day streak.", "icon_30.png"),

                create("Challenge Creator", "CREATOR",
                        "Awarded for creating your first challenge.", "icon_creator.png"),

                create("Challenge Finisher", "FINISHER",
                        "Awarded for completing a challenge successfully.", "icon_finisher.png"),

                create("Top Performer", "TOP_PERFORMER",
                        "Given to the highest points holder in a challenge.", "icon_gold.png")
        );

        for (Badge badge : systemBadges) {
            if (badgeRepository.findByCode(badge.getCode()).isEmpty()) {
                badgeRepository.save(badge);
            }
        }
    }

    private Badge create(String name, String code, String description, String icon) {
        return Badge.builder()
                .name(name)
                .code(code)
                .description(description)
                .iconUrl(icon)
                .badgeType(BadgeType.SYSTEM)
                .createdAt(LocalDateTime.now())
                .challengeId(null)
                .build();
    }
}
