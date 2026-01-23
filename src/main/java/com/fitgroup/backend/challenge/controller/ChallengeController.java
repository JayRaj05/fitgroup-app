package com.fitgroup.backend.challenge.controller;

import com.fitgroup.backend.challenge.dto.CreateChallengeRequest;
import com.fitgroup.backend.challenge.service.ChallengeService;
import com.fitgroup.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserRepository userRepository;

    // ---------------------------------------------------
    // Helper: Convert JWT → userId
    // ---------------------------------------------------
    private Long getUserId(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    // ---------------------------------------------------
    // 1) Create Personal Challenge
    // ---------------------------------------------------
    @PostMapping("/personal")
    public ResponseEntity<?> createPersonalChallenge(
            @RequestBody CreateChallengeRequest request,
            Principal principal)
    {
        Long userId = getUserId(principal);
        challengeService.createPersonalChallenge(request, userId);
        return ResponseEntity.ok("Personal challenge created successfully");
    }

    // ---------------------------------------------------
    // 2) Create Group Challenge
    // ---------------------------------------------------
    @PostMapping("/group")
    public ResponseEntity<?> createGroupChallenge(
            @RequestBody CreateChallengeRequest request,
            Principal principal
    ) {
        Long userId = getUserId(principal);
        challengeService.createGroupChallenge(request, userId);
        return ResponseEntity.ok("Group challenge created successfully");
    }

    // ---------------------------------------------------
    // 3) Get My Challenges
    // ---------------------------------------------------
    @GetMapping("/my")
    public ResponseEntity<?> getMyChallenges(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(challengeService.getMyChallenges(userId));
    }

    // ---------------------------------------------------
    // 4) Join a Public Group Challenge
    // ---------------------------------------------------
    @PostMapping("/{challengeId}/join")
    public ResponseEntity<?> joinChallenge(
            @PathVariable Long challengeId,
            Principal principal
    ) {
        Long userId = getUserId(principal);
        challengeService.joinChallenge(challengeId, userId);
        return ResponseEntity.ok("Joined challenge successfully");
    }

    // ---------------------------------------------------
    // 5) Browse Public Challenges
    // ---------------------------------------------------
    @GetMapping("/public")
    public ResponseEntity<?> getPublicChallenges() {
        return ResponseEntity.ok(challengeService.getPublicChallenges());
    }

    // ---------------------------------------------------
    // 6) Get Challenge Details by ID
    // ---------------------------------------------------
    @GetMapping("/{challengeId}")
    public ResponseEntity<?> getChallengeDetails(@PathVariable Long challengeId) {
        return ResponseEntity.ok(challengeService.getChallengeDetails(challengeId));
    }

    // ---------------------------------------------------
    // 7) Update Challenge (OWNER only)
    // ---------------------------------------------------
    @PutMapping("/{challengeId}")
    public ResponseEntity<?> updateChallenge(
            @PathVariable Long challengeId,
            @RequestBody CreateChallengeRequest request,
            Principal principal
    ) {
        Long userId = getUserId(principal);
        challengeService.updateChallenge(challengeId, request, userId);
        return ResponseEntity.ok("Challenge updated successfully");
    }

    // ---------------------------------------------------
    // 8) Delete (Soft Delete) Challenge (OWNER only)
    // ---------------------------------------------------
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<?> deleteChallenge(
            @PathVariable Long challengeId,
            Principal principal
    ) {
        Long userId = getUserId(principal);
        challengeService.deleteChallenge(challengeId, userId);
        return ResponseEntity.ok("Challenge deleted");
    }

    @GetMapping("/{challengeId}/leaderboard")
    public ResponseEntity<?> getLeaderboard(
            @PathVariable Long challengeId
    ) {
        return ResponseEntity.ok(challengeService.getLeaderboard(challengeId));
    }

    @PostMapping("/{challengeId}/finish")
    public ResponseEntity<?> finishChallenge(
            @PathVariable Long challengeId,
            Principal principal
    ) {
        Long userId = getUserId(principal);
        challengeService.finishChallengeFlow(challengeId, userId);
        return ResponseEntity.ok("Challenge completed and badges awarded.");
    }



}