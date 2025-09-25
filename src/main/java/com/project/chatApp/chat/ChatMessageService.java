package com.project.chatApp.chat;

import com.project.chatApp.chat.dto.CreateDTO;
import com.project.chatApp.chat.dto.DeleteNotificationDTO;

import java.util.List;

public interface ChatMessageService {

    ChatMessage privateSave(CreateDTO createDTO, String senderId);

    ChatMessage groupSave(CreateDTO createDTO, String userId);

    DeleteNotificationDTO delete(String senderId, String messageId);

    List<ChatMessage> getChatMessages(String chatId);
}
