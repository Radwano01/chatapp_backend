package com.project.chatApp.chat.dto;

import com.project.chatApp.chat.MessageType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatNotification {
    private String id;
    private String chatId;
    private String senderId;
    private String recipientId;
    private String media;
    private String content;
    private String avatar;
    private MessageType type;

    private String senderName;
    private String senderAvatar;
}
