package com.project.chatApp.chatRoom;


import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ChatRoomRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void chatRoomRepository_findChatRoomByChatId_returnNotNull() {
        var user = User.builder().build();

        var chatroom = ChatRoom.builder()
                .chatId("123")
                .members(List.of(user))
                .build();

        userRepository.save(user);
        chatRoomRepository.save(chatroom);
        var savedChatRoom = chatRoomRepository.findChatRoomByChatId("123");

        Assertions.assertNotNull(savedChatRoom);
    }

    @Test
    void ChatRoomRepository_findAllByMember_returnMoreThenOneChatRoom() {
        var user = User.builder().build();

        var chatroom = ChatRoom.builder()
                .chatId("123")
                .members(List.of(user))
                .build();

        var chatroom2 = ChatRoom.builder()
                .chatId("123")
                .members(List.of(user))
                .build();

        userRepository.save(user);
        chatRoomRepository.saveAll(List.of(chatroom, chatroom2));

        var savedChatRooms = chatRoomRepository.findAllByMember(user.getId());

        Assertions.assertNotNull(savedChatRooms);
        Assertions.assertEquals(2, savedChatRooms.size());
    }
}