package com.project.chatApp.chatRoom;

import com.project.chatApp.exception.ResourceNotFoundException;
import com.project.chatApp.user.Role;
import com.project.chatApp.user.Status;
import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ChatRoomServiceImplTest {

    @Mock
    ChatRoomRepository chatRoomRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ChatRoomServiceImpl chatRoomService;

    private ChatRoom chatRoom;

    private User user;

    private User user2;

    @BeforeEach
    void init() {
        user = User.builder()
                .id("1")
                .username("username")
                .fullName("Full Name")
                .description("desc")
                .avatar("avatar")
                .status(Status.OFFLINE)
                .role(Role.USER)
                .build();

        user2 = User.builder()
                .id("2")
                .username("username2")
                .fullName("Full Name2")
                .description("desc2")
                .avatar("avatar2")
                .status(Status.OFFLINE)
                .role(Role.USER)
                .build();

        chatRoom = ChatRoom.builder()
                .id(1)
                .chatId("1_2")
                .members(List.of(user, user2))
                .build();
    }

    @Test
    void chatRoomService_getOrCreatePrivateRoom_returnChatRoomDYO(){
        Date now = new Date();
        chatRoom.setCreatedAt(now);

        when(userRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(user, user2));
        when(chatRoomRepository.findChatRoomByChatId("1_2")).thenReturn(Optional.of(chatRoom));

        var savedChatRoom = chatRoomService.getOrCreatePrivateRoom("1", "2");

        assertAll(
                ()-> assertEquals(chatRoom.getChatId(), savedChatRoom.getChatId()),
                ()-> assertEquals(chatRoom.getId(), savedChatRoom.getId()),
                ()-> assertEquals(chatRoom.getMembers(), savedChatRoom.getMembers()),
                ()-> assertEquals(now, savedChatRoom.getCreatedAt())
        );
    }

    @Test
    void chatRoomService_getOrCreatePrivateRoom_createChatRoomCase_returnChatRoomDYO(){
        Date now = new Date();
        ChatRoom newRoom = ChatRoom.builder()
                .id(1)
                .chatId("1_2")
                .members(List.of(user, user2))
                .createdAt(now)
                .build();

        when(userRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(user, user2));
        when(chatRoomRepository.findChatRoomByChatId("1_2")).thenReturn(Optional.empty());
        when(chatRoomRepository.save(Mockito.any(ChatRoom.class))).thenReturn(newRoom);

        var savedChatRoom = chatRoomService.getOrCreatePrivateRoom("1", "2");

        assertAll(
                ()-> assertEquals(chatRoom.getChatId(), savedChatRoom.getChatId()),
                ()-> assertEquals(chatRoom.getId(), savedChatRoom.getId()),
                ()-> assertEquals(chatRoom.getMembers(), savedChatRoom.getMembers()),
                ()-> assertEquals(now, savedChatRoom.getCreatedAt())
        );

        verify(chatRoomRepository, times(1)).save(Mockito.any(ChatRoom.class));
    }

    @Test
    void chatRoomService_getOrCreatePrivateRoom_returnNotFoundException(){
        when(userRepository.findAllById(List.of("3", "4"))).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, ()-> chatRoomService.getOrCreatePrivateRoom("3", "4"));
    }

    @Test
    void chatRoomService_getUserChatRooms_returnChatRoomDTOs(){
        when(chatRoomRepository.findAllByMember("1")).thenReturn(List.of(chatRoom));
        when(userRepository.findById("2")).thenReturn(Optional.of(user2));

        var savedUserChatRooms = chatRoomService.getUserChatRooms("1");

        assertAll(
                () -> assertEquals(chatRoom.getId(), savedUserChatRooms.get(0).getId()),
                () -> assertEquals(chatRoom.getChatId(), savedUserChatRooms.get(0).getChatId()),
                () -> assertEquals("2", savedUserChatRooms.get(0).getOtherUserId()),
                () -> assertEquals(user2.getFullName(), savedUserChatRooms.get(0).getFullName()),
                () -> assertEquals(user2.getAvatar(), savedUserChatRooms.get(0).getAvatar())
        );
    }

    @Test
    void chatRoomService_getRecipientId_returnRecipientId(){
        when(chatRoomRepository.findChatRoomByChatId("1_2")).thenReturn(Optional.of(chatRoom));

        var savedRecipientId = chatRoomService.getRecipientId("1_2", "1");

        assertEquals(user2.getId(), savedRecipientId);
    }
}