package com.fitgroup.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SendMessageRequest {
    private String messageText;
}