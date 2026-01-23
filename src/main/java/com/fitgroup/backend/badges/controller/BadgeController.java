package com.fitgroup.backend.badges.controller;

import com.fitgroup.backend.badges.service.BadgeAwardService;
import com.fitgroup.backend.badges.dto.UserBadgeResponse;
import com.fitgroup.backend.badges.service.BadgeAwardService;
import com.fitgroup.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeAwardService badgeService;
    private final UserRepository userRepository;

    private Long getUserId(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBadges(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(badgeService.getUserBadgeDetails(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }
}