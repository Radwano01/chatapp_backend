package com.project.chatApp.chat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.messaging.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ChatMessageRepositoryTest {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp(){
        chatMessageRepository.deleteAll();
    }

    @Test
    void ChatMessageRepository_findAllByChatId_returnMoreThenOneMessage() {
        var message = ChatMessage.builder()
                .chatId("chatId")
                .build();

        var message2 = ChatMessage.builder()
                .chatId("chatId")
                .build();

        chatMessageRepository.saveAll(List.of(message, message2));
        var savedMessages = chatMessageRepository.findAllByChatId("chatId");

        Assertions.assertEquals(2, savedMessages.size());
    }
}