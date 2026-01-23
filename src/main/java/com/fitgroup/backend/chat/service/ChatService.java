package com.fitgroup.backend.chat.service;

import com.fitgroup.backend.chat.entity.ChatMessage;
import com.fitgroup.backend.chat.repository.ChatMessageRepository;
import com.fitgroup.backend.challenge.entity.Challenge;
import com.fitgroup.backend.challenge.repository.ChallengeParticipantRepository;
import com.fitgroup.backend.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeParticipantRepository participantRepository;

    // -------------------------------------------------
    // SEND MESSAGE
    // -------------------------------------------------
    public void sendMessage(Long challengeId, Long userId, String messageText) {

        // 1️⃣ Challenge must exist & be active
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (!challenge.getIsActive() || challenge.getIsDeleted()) {
            throw new RuntimeException("Chat is closed for this challenge");
        }

        // 2️⃣ User must be participant
        boolean isParticipant =
                participantRepository.existsByChallengeIdAndUserId(challengeId, userId);

        if (!isParticipant) {
            throw new RuntimeException("User is not part of this challenge");
        }

        // 3️⃣ Save message
        ChatMessage message = ChatMessage.builder()
                .challengeId(challengeId)
                .userId(userId)
                .messageText(messageText)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
    }

    // -------------------------------------------------
    // FETCH MESSAGES
    // -------------------------------------------------
    public List<ChatMessage> getMessages(Long challengeId, Long userId) {

        // 1️⃣ Challenge must exist
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        // 2️⃣ User must be participant
        boolean isParticipant =
                participantRepository.existsByChallengeIdAndUserId(challengeId, userId);

        if (!isParticipant) {
            throw new RuntimeException("User is not part of this challenge");
        }

        // 3️⃣ Return ordered chat
        return chatMessageRepository
                .findByChallengeIdOrderByCreatedAtAsc(challengeId);
    }
}