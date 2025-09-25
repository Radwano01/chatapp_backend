package com.project.chatApp.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteNotificationDTO {
    private String messageId;
    private String chatId;
    private String senderId;
    private boolean isDeleted;
}
