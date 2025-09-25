package com.project.chatApp.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private String chatId;
    private String senderId;

    private MessageType type;
    private String content;
    private String media;
    private Integer duration;

    private String senderName;
    private String senderAvatar;

    private boolean deleted;

    private Date timestamp;
}
