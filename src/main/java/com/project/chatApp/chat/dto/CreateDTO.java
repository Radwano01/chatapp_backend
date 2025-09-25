package com.project.chatApp.chat.dto;


import com.project.chatApp.chat.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDTO {
    private String chatId;

    private MessageType type;
    private String content;
    private String media;
    private Integer duration;

    private String senderName;
    private String senderAvatar;
}