package com.project.chatApp.chat;

import com.project.chatApp.chat.dto.CreateDTO;
import com.project.chatApp.chatRoom.ChatRoom;
import com.project.chatApp.chatRoom.ChatRoomService;
import com.project.chatApp.exception.ResourceDoesNotHaveAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    ChatMessageRepository chatMessageRepository;

    @Mock
    ChatRoomService chatRoomService;

    @InjectMocks
    ChatMessageServiceImpl chatMessageService;

    @Test
    void chatMessage_privateSave_returnChatMessageDTO(){
        CreateDTO message = CreateDTO.builder()
                .chatId("1_2")
                .type(MessageType.TEXT)
                .content("test")
                .senderName("user")
                .senderAvatar("avatar")
                .build();
        var chatRoom = ChatRoom.builder()
                .chatId("1_2")
                .build();

        when(chatRoomService.getRecipientId("1_2", "1")).thenReturn("2");
        when(chatRoomService.getOrCreatePrivateRoom("1", "2")).thenReturn("1_2");

        var savedPrivateMessage = chatMessageService.privateSave(message, "1");

        assertAll(
                ()-> assertEquals(message.getChatId(), savedPrivateMessage.getChatId()),
                ()-> assertEquals("1", savedPrivateMessage.getSenderId()),
                ()-> assertEquals(message.getSenderName(), savedPrivateMessage.getSenderName()),
                ()-> assertEquals(message.getSenderAvatar(), savedPrivateMessage.getSenderAvatar()),
                ()-> assertEquals(message.getType(), savedPrivateMessage.getType()),
                ()-> assertEquals(message.getContent(), savedPrivateMessage.getContent()),
                ()-> assertEquals(message.getMedia(), savedPrivateMessage.getMedia()),
                ()-> assertEquals(message.getDuration(), savedPrivateMessage.getDuration())
        );
        verify(chatMessageRepository, times(1)).save(savedPrivateMessage);
    }

    @Test
    void chatMessage_groupSave_returnChatMessageDTO(){
        CreateDTO message = CreateDTO.builder()
                .chatId("1_2")
                .type(MessageType.TEXT)
                .content("test")
                .senderName("user")
                .senderAvatar("avatar")
                .build();

        var savedGroupMessage = chatMessageService.groupSave(message, "1");

        assertAll(
                ()-> assertEquals(message.getChatId(), savedGroupMessage.getChatId()),
                ()-> assertEquals("1", savedGroupMessage.getSenderId()),
                ()-> assertEquals(message.getSenderName(), savedGroupMessage.getSenderName()),
                ()-> assertEquals(message.getSenderAvatar(), savedGroupMessage.getSenderAvatar()),
                ()-> assertEquals(message.getType(), savedGroupMessage.getType()),
                ()-> assertEquals(message.getContent(), savedGroupMessage.getContent()),
                ()-> assertEquals(message.getMedia(), savedGroupMessage.getMedia()),
                ()-> assertEquals(message.getDuration(), savedGroupMessage.getDuration())
        );
        verify(chatMessageRepository, times(1)).save(savedGroupMessage);
    }

    @Test
    void chatMessage_delete_returnDeleted(){
        var message = ChatMessage.builder()
                .id("messageId")
                .chatId("1_2")
                .senderId("1")
                .deleted(true)
                .build();

        when(chatMessageRepository.findById("messageId")).thenReturn(Optional.of(message));

        var deletedMessage = chatMessageService.delete("1", "messageId");

        assertAll(
                ()-> assertEquals("messageId", deletedMessage.getMessageId()),
                ()-> assertEquals("1_2", deletedMessage.getChatId()),
                ()-> assertEquals("1", deletedMessage.getSenderId()),
                ()-> assertTrue(deletedMessage.isDeleted())
        );
        verify(chatMessageRepository, times(1)).save(Mockito.any(ChatMessage.class));
    }

    @Test
    void chatMessage_delete_returnAccessException(){
        var message = ChatMessage.builder()
                .id("messageId")
                .chatId("1_2")
                .senderId("1")
                .deleted(true)
                .build();

        when(chatMessageRepository.findById("messageId")).thenReturn(Optional.of(message));

        assertThrows(ResourceDoesNotHaveAccessException.class, ()-> chatMessageService.delete("2", "messageId"));
    }

    @Test
    void chatMessage_getChatMessages_returnChatMessageDTOs() {
        Date now = new Date();

        var message1 = ChatMessage.builder()
                .id("m1")
                .chatId("1_2")
                .senderId("1")
                .senderName("Alice")
                .senderAvatar("avatar1.png")
                .type(MessageType.TEXT)
                .duration(0)
                .content("Hello")
                .media(null)
                .deleted(false)
                .timestamp(now)
                .build();

        var message2 = ChatMessage.builder()
                .id("m2")
                .chatId("1_2")
                .senderId("2")
                .senderName("Bob")
                .senderAvatar("avatar2.png")
                .type(MessageType.IMAGE)
                .duration(5)
                .content("Image caption")
                .media("image.png")
                .deleted(true)
                .timestamp(now)
                .build();

        when(chatMessageRepository.findAllByChatId("1_2")).thenReturn(List.of(message1, message2));

        var result = chatMessageService.getChatMessages("1_2");

        assertAll(
                () -> assertEquals("m1", result.get(0).getId()),
                () -> assertEquals("1_2", result.get(0).getChatId()),
                () -> assertEquals("1", result.get(0).getSenderId()),
                () -> assertEquals("Alice", result.get(0).getSenderName()),
                () -> assertEquals("avatar1.png", result.get(0).getSenderAvatar()),
                () -> assertEquals(MessageType.TEXT, result.get(0).getType()),
                () -> assertEquals(0, result.get(0).getDuration()),
                () -> assertEquals("Hello", result.get(0).getContent()),
                () -> assertNull(result.get(0).getMedia()),
                () -> assertFalse(result.get(0).isDeleted()),
                () -> assertEquals(now, result.get(0).getTimestamp())
        );

        verify(chatMessageRepository, times(1)).findAllByChatId("1_2");
    }

}