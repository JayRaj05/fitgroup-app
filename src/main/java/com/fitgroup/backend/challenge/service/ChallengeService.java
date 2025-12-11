package com.fitgroup.backend.challenge.service;

import com.fitgroup.backend.badges.engine.BadgeRuleEngine;
import com.fitgroup.backend.challenge.dto.CreateChallengeRequest;
import com.fitgroup.backend.challenge.dto.LeaderboardEntry;
import com.fitgroup.backend.challenge.dto.MyChallengeResponse;
import com.fitgroup.backend.challenge.entity.Challenge;
import com.fitgroup.backend.challenge.entity.ChallengeParticipant;
import com.fitgroup.backend.challenge.enums.ChallengeType;
import com.fitgroup.backend.challenge.enums.GoalType;
import com.fitgroup.backend.challenge.enums.ParticipantRole;
import com.fitgroup.backend.challenge.enums.Visibility;
import com.fitgroup.backend.challenge.repository.ChallengeParticipantRepository;
import com.fitgroup.backend.challenge.repository.ChallengeRepository;
import com.fitgroup.backend.checkin.repository.DailyCheckinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;
    private final BadgeRuleEngine badgeRuleEngine;
    private final DailyCheckinRepository dailyCheckinRepository;


    // -------------------------------------------------------------
    // CREATE PERSONAL CHALLENGE
    // -------------------------------------------------------------
    public void createPersonalChallenge(CreateChallengeRequest request, Long userId) {

        validateDates(request);

        Challenge challenge = Challenge.builder()
                .name(request.name)
                .description(request.description)
                .type(ChallengeType.PERSONAL)
                .createdBy(userId)
                .visibility(Visibility.PRIVATE)
                .goalType(GoalType.valueOf(request.goalType.toUpperCase()))
                .targetValue(request.targetValue)
                .startDate(request.startDate)
                .endDate(request.endDate)
                .isActive(true)
                .isDeleted(false)
                .participantCount(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        Challenge saved = challengeRepository.save(challenge);

        participantRepository.save(
                ChallengeParticipant.builder()
                        .challengeId(saved.getId())
                        .userId(userId)
                        .role(ParticipantRole.OWNER)
                        .joinedAt(LocalDateTime.now())
                        .totalPoints(0)
                        .currentStreak(0)
                        .maxStreak(0)
                        .build()
        );


        badgeRuleEngine.onChallengeCreated(userId);
    }

    // -------------------------------------------------------------
    // CREATE GROUP CHALLENGE
    // -------------------------------------------------------------
    public void createGroupChallenge(CreateChallengeRequest request, Long userId) {

        validateDates(request);

        Challenge challenge = Challenge.builder()
                .name(request.name)
                .description(request.description)
                .type(ChallengeType.GROUP)
                .visibility(request.visibility != null ? request.visibility : Visibility.PUBLIC)
                .createdBy(userId)
                .goalType(GoalType.valueOf(request.goalType.toUpperCase()))
                .targetValue(request.targetValue)
                .startDate(request.startDate)
                .endDate(request.endDate)
                .isActive(true)
                .isDeleted(false)
                .participantCount(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Challenge saved = challengeRepository.save(challenge);

        participantRepository.save(
                ChallengeParticipant.builder()
                        .challengeId(saved.getId())
                        .userId(userId)
                        .role(ParticipantRole.OWNER)
                        .joinedAt(LocalDateTime.now())
                        .totalPoints(0)
                        .currentStreak(0)
                        .maxStreak(0)
                        .build()
        );

        badgeRuleEngine.onChallengeCreated(userId);
    }

    // -------------------------------------------------------------
    // GET MY CHALLENGES
    // -------------------------------------------------------------
    public List<MyChallengeResponse> getMyChallenges(Long userId) {

        List<Object[]> rows = participantRepository.findAllByUserWithChallenges(userId);

        return rows.stream().map(row -> {
            ChallengeParticipant cp = (ChallengeParticipant) row[0];
            Challenge c = (Challenge) row[1];

            return MyChallengeResponse.builder()
                    .challengeId(c.getId())
                    .name(c.getName())
                    .description(c.getDescription())
                    .type(c.getType())
                    .visibility(c.getVisibility())
                    .goalType(c.getGoalType())
                    .targetValue(c.getTargetValue())
                    .startDate(c.getStartDate())
                    .endDate(c.getEndDate())
                    .role(cp.getRole())
                    .totalPoints(cp.getTotalPoints())
                    .currentStreak(cp.getCurrentStreak())
                    .maxStreak(cp.getMaxStreak())
                    .build();
        }).toList();
    }

    // -------------------------------------------------------------
    // JOIN CHALLENGE
    // -------------------------------------------------------------
    public void joinChallenge(Long challengeId, Long userId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (participantRepository.existsByChallengeIdAndUserId(challengeId, userId)) {
            throw new RuntimeException("User already joined this challenge");
        }

        if (challenge.getType() == ChallengeType.PERSONAL) {
            throw new RuntimeException("Cannot join personal challenges");
        }

        if (challenge.getVisibility() == Visibility.PRIVATE) {
            throw new RuntimeException("This challenge is private");
        }

        participantRepository.save(
                ChallengeParticipant.builder()
                        .challengeId(challengeId)
                        .userId(userId)
                        .role(ParticipantRole.PARTICIPANT)
                        .joinedAt(LocalDateTime.now())
                        .totalPoints(0)
                        .currentStreak(0)
                        .maxStreak(0)
                        .build()
        );

        challenge.setParticipantCount(challenge.getParticipantCount() + 1);
        challengeRepository.save(challenge);
    }

    // -------------------------------------------------------------
    // NEW: GET PUBLIC CHALLENGES
    // -------------------------------------------------------------
    public List<Challenge> getPublicChallenges() {
        return challengeRepository.findAll().stream()
                .filter(c -> !c.getIsDeleted())
                .filter(c -> c.getVisibility() == Visibility.PUBLIC)
                .filter(c -> c.getType() == ChallengeType.GROUP)
                .filter(Challenge::getIsActive)
                .toList();
    }

    // -------------------------------------------------------------
    // NEW: GET CHALLENGE DETAILS
    // -------------------------------------------------------------
    public Challenge getChallengeDetails(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    // -------------------------------------------------------------
    // NEW: UPDATE CHALLENGE (OWNER ONLY)
    // -------------------------------------------------------------
    public void updateChallenge(Long challengeId, CreateChallengeRequest req, Long userId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (!challenge.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only the challenge owner can update the challenge");
        }

        validateDates(req);

        challenge.setName(req.name);
        challenge.setDescription(req.description);
        challenge.setGoalType(GoalType.valueOf(req.goalType.toUpperCase()));
        challenge.setTargetValue(req.targetValue);
        challenge.setStartDate(req.startDate);
        challenge.setEndDate(req.endDate);
        challenge.setVisibility(req.visibility);
        challenge.setUpdatedAt(LocalDateTime.now());

        challengeRepository.save(challenge);
    }

    // -------------------------------------------------------------
    // NEW: SOFT DELETE CHALLENGE (OWNER ONLY)
    // -------------------------------------------------------------
    public void deleteChallenge(Long challengeId, Long userId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (!challenge.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only the challenge owner can delete this challenge");
        }

        challenge.setIsDeleted(true);
        challenge.setIsActive(false);
        challenge.setUpdatedAt(LocalDateTime.now());

        challengeRepository.save(challenge);
    }

    // -------------------------------------------------------------
    // VALIDATION LOGIC
    // -------------------------------------------------------------
    private void validateDates(CreateChallengeRequest request) {
        if (request.endDate.isBefore(request.startDate)) {
            throw new RuntimeException("End date cannot be before start date.");
        }

        if (request.startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the past.");
        }

        if (request.startDate.equals(request.endDate)) {
            throw new RuntimeException("Challenge must be at least 1 day long.");
        }
    }

    public List<LeaderboardEntry> getLeaderboard(Long challengeId) {

        List<Object[]> rows = participantRepository.findLeaderboard(challengeId);

        final int[] rank = {1};

        return rows.stream().map(row -> {

            ChallengeParticipant cp = (ChallengeParticipant) row[0];
            String fullName = (String) row[1];

            LeaderboardEntry entry = LeaderboardEntry.builder()
                    .userId(cp.getUserId())
                    .fullName(fullName)
                    .totalPoints(cp.getTotalPoints())
                    .currentStreak(cp.getCurrentStreak())
                    .maxStreak(cp.getMaxStreak())
                    .rank(rank[0])
                    .build();

            rank[0]++;

            return entry;

        }).toList();
    }

    public void completeChallenge(Long challengeId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (!challenge.getIsActive()) {
            throw new RuntimeException("Challenge already finished");
        }

        // Mark challenge inactive
        challenge.setIsActive(false);
        challenge.setUpdatedAt(LocalDateTime.now());
        challengeRepository.save(challenge);
    }

    public void awardFinisherBadges(Long challengeId) {

        List<ChallengeParticipant> participants =
                participantRepository.findByChallengeId(challengeId);

        for (ChallengeParticipant p : participants) {
            badgeRuleEngine.onChallengeFinished(p.getUserId(), challengeId);
        }
    }

    public void awardTopPerformerBadge(Long challengeId) {

        List<ChallengeParticipant> participants =
                participantRepository.findByChallengeIdOrderByTotalPointsDesc(challengeId);

        if (participants.isEmpty()) return;

        ChallengeParticipant winner = participants.get(0);

        badgeRuleEngine.awardTopPerformer(winner.getUserId(), challengeId);
    }

    public void finishChallengeFlow(Long challengeId, Long userId) {

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (!challenge.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only the owner can finish the challenge");
        }

        // Step 1: close it
        completeChallenge(challengeId);

        // Step 2: award finisher badges
        awardFinisherBadges(challengeId);

        // Step 3: award top performer badge
        awardTopPerformerBadge(challengeId);
    }
}



