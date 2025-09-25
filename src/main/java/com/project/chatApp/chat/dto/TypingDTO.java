package com.project.chatApp.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TypingDTO {
    private String chatId;
    private String senderId;
    private String fullName;
    private String username;
    private boolean typing;
    private boolean group;
}
