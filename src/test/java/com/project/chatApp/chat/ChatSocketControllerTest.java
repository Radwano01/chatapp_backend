package com.project.chatApp.chat;

import com.project.chatApp.chat.dto.*;
import com.project.chatApp.chatRoom.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ChatSocketControllerTest {

    @Mock
    SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    ChatMessageService chatMessageService;

    @Mock
    ChatRoomService chatRoomService;

    @InjectMocks
    ChatSocketController chatSocketController;

    @Test
    void chatController_saveQueue_returnMessageDTO() {
        CreateDTO createDTO = CreateDTO.builder()
                .chatId("chat1")
                .content("test")
                .build();

        ChatMessage savedMessage = ChatMessage.builder()
                .id("msg1")
                .chatId("chat1")
                .senderId("1")
                .content("Hello")
                .senderName("User One")
                .senderAvatar("avatar1")
                .media("media1")
                .build();

        when(chatMessageService.privateSave(createDTO, "1")).thenReturn(savedMessage);
        when(chatRoomService.getRecipientId("chat1", "1")).thenReturn("2");

        chatSocketController.saveQueue(createDTO, () -> "1");

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("2"),
                eq("/queue/chatroom/chat1"),
                any(ChatNotification.class)
        );
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("1"),
                eq("/queue/chatroom/chat1"),
                any(ChatNotification.class)
        );
    }

    @Test
    void chatController_saveTopic_returnChatMessageDTO(){
        CreateDTO createDTO = CreateDTO.builder()
                .chatId("group1")
                .content("test")
                .build();

        ChatMessage savedMessage = ChatMessage.builder()
                .id("msg2")
                .chatId("group1")
                .senderId("user1")
                .content("Group message")
                .build();

        when(chatMessageService.groupSave(createDTO, "1")).thenReturn(savedMessage);

        chatSocketController.saveTopic(createDTO, () -> "1");

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/chatroom/group1"),
                any(ChatNotification.class)
        );
    }

    @Test
    void chatController_typing_returnTyping(){
        TypingDTO typingDTO = TypingDTO.builder()
                .chatId("chat1")
                .group(false)
                .build();

        when(chatRoomService.getRecipientId("chat1", "1")).thenReturn("2");

        chatSocketController.typing(typingDTO, ()-> "1");

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("2"),
                eq("/queue/chatroom/chat1typing"),
                eq(typingDTO)
        );
    }

    @Test
    void chatController_deleteQueue_returnDeleted(){
        DeleteDTO deleteDTO = DeleteDTO.builder()
                .messageId("msg1")
                .build();

        DeleteNotificationDTO deletedMessage = DeleteNotificationDTO.builder()
                .messageId("msg1")
                .chatId("chat1")
                .senderId("1")
                .build();

        when(chatMessageService.delete("1", "msg1")).thenReturn(deletedMessage);
        when(chatRoomService.getRecipientId("chat1", "1")).thenReturn("2");

        chatSocketController.deleteQueue(deleteDTO, ()->"1");

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("2"),
                eq("/queue/chatroom/chat1"),
                eq(deletedMessage)
        );
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("1"),
                eq("/queue/chatroom/chat1"),
                eq(deletedMessage)
        );
    }

    @Test
    void chatController_deleteTopic_returnDeleted() {
        DeleteDTO deleteDTO = DeleteDTO.builder()
                .messageId("msg2")
                .build();

        DeleteNotificationDTO deletedMessage = DeleteNotificationDTO.builder()
                .chatId("group1")
                .senderId("1")
                .build();

        when(chatMessageService.delete("1", "msg2")).thenReturn(deletedMessage);

        chatSocketController.deleteTopic(deleteDTO, ()->"1");

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/chatroom/group1"),
                eq(deletedMessage)
        );
    }
}