package com.fitgroup.backend.chat.repository;

import com.fitgroup.backend.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChallengeIdOrderByCreatedAtAsc(Long challengeId);

}
