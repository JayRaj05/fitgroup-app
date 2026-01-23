package com.fitgroup.backend.chat.controller;

import com.fitgroup.backend.chat.dto.SendMessageRequest;
import com.fitgroup.backend.chat.entity.ChatMessage;
import com.fitgroup.backend.chat.service.ChatService;
import com.fitgroup.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/challenges/{challengeId}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    // -----------------------------------------
    // Helper: JWT → userId
    // -----------------------------------------
    private Long getUserId(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    // -----------------------------------------
    // SEND MESSAGE
    // -----------------------------------------
    @PostMapping
    public ResponseEntity<?> sendMessage(
            @PathVariable Long challengeId,
            @RequestBody SendMessageRequest request,
            Principal principal
    ) {
        Long userId = getUserId(principal);

        chatService.sendMessage(
                challengeId,
                userId,
                request.getMessageText()
        );
        return ResponseEntity.ok("Message sent");
    }

    // -----------------------------------------
    // FETCH CHAT MESSAGES
    // -----------------------------------------
    @GetMapping
    public ResponseEntity<List<ChatMessage>> getMessages(
            @PathVariable Long challengeId,
            Principal principal
    ) {
        Long userId = getUserId(principal);

        return ResponseEntity.ok(
                chatService.getMessages(challengeId, userId)
        );


    }
}