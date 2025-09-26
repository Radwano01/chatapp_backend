package com.project.chatApp.chat;

import com.project.chatApp.chat.dto.*;
import com.project.chatApp.chatRoom.ChatRoomService;
import com.project.chatApp.exception.ResourceDoesNotHaveAccessException;
import com.project.chatApp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    @Override
    public ChatMessage privateSave(CreateDTO createDTO, String senderId) {
        var recipientId = chatRoomService.getRecipientId(createDTO.getChatId(), senderId);

        var chatId = chatRoomService.getOrCreatePrivateRoom(senderId, recipientId);

        var chatMessage = ChatMessage.builder()
                .chatId(chatId)
                .senderId(senderId)
                .senderName(createDTO.getSenderName())
                .senderAvatar(createDTO.getSenderAvatar())
                .type(createDTO.getType())
                .duration(createDTO.getDuration())
                .content(createDTO.getContent())
                .media(createDTO.getMedia())
                .deleted(false)
                .timestamp(new Date())
                .build();

        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public ChatMessage groupSave(CreateDTO createDTO, String userId) {
        var chatMessage = ChatMessage.builder()
                .chatId(createDTO.getChatId())
                .senderId(userId)
                .senderName(createDTO.getSenderName())
                .senderAvatar(createDTO.getSenderAvatar())
                .type(createDTO.getType())
                .duration(createDTO.getDuration())
                .content(createDTO.getContent())
                .media(createDTO.getMedia())
                .deleted(false)
                .build();

        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public DeleteNotificationDTO delete(String senderId, String messageId) {
        var savedMessage = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if(!savedMessage.getSenderId().equals(senderId)){
            throw new ResourceDoesNotHaveAccessException("User does not have access");
        }

        savedMessage.setDeleted(true);
        chatMessageRepository.save(savedMessage);

        return DeleteNotificationDTO.builder()
                .messageId(savedMessage.getId())
                .chatId(savedMessage.getChatId())
                .senderId(savedMessage.getSenderId())
                .isDeleted(savedMessage.isDeleted())
                .build();
    }

    @Override
    public List<ChatMessage> getChatMessages(String chatId) {
        var chatMessages = chatMessageRepository.findAllByChatId(chatId);

        return chatMessages.stream()
                .map(msg -> ChatMessage.builder()
                        .id(msg.getId())
                        .chatId(msg.getChatId())
                        .senderId(msg.getSenderId())
                        .senderName(msg.getSenderName())
                        .senderAvatar(msg.getSenderAvatar())
                        .type(msg.getType())
                        .duration(msg.getDuration())
                        .content(msg.getContent())
                        .media(msg.getMedia())
                        .deleted(msg.isDeleted())
                        .timestamp(msg.getTimestamp())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
