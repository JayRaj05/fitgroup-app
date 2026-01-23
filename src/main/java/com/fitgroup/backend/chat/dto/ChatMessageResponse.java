package com.fitgroup.backend.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {

    private Long messageId;
    private Long userId;
    private String fullName;
    private String message;
    private LocalDateTime createdAt;
}
