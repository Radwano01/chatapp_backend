package com.project.chatApp.friend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FriendRepositoryTest {

    @Autowired
    FriendRepository friendRepository;

    @Test
    void friendRepository_findAllByUserId_returnMoreThenOneFriend(){
        var relation = Friend.builder()
                .userId("123")
                .friendId("456")
                .status(Status.PENDING)
                .build();

        var relation2 = Friend.builder()
                .userId("123")
                .friendId("789")
                .status(Status.PENDING)
                .build();


        friendRepository.saveAll(List.of(relation, relation2));
        var savedRelations = friendRepository.findAllByUserId("123");

        Assertions.assertEquals(2, savedRelations.size());
    }

    @Test
    void friendRepository_existsByUserIdAndFriendId_returnTrue(){
        var relation = Friend.builder()
                .userId("123")
                .friendId("456")
                .status(Status.PENDING)
                .build();


        friendRepository.save(relation);
        var savedRelation = friendRepository.existsByUserIdAndFriendId("123", "456")
                .orElse(null);

        Assertions.assertNotNull(savedRelation);
        Assertions.assertEquals(true, savedRelation);
    }


    @Test
    void friendRepository_deleteByUserIdAndFriendId_returnNull(){
        var relation = Friend.builder()
                .userId("123")
                .friendId("456")
                .status(Status.PENDING)
                .build();

        friendRepository.save(relation);
        friendRepository.deleteByUserIdAndFriendId("123", "456");
        var savedRelation = friendRepository.findByUserIdAndFriendId("123", "456");

        Assertions.assertEquals(Optional.empty(), savedRelation);
    }

    @Test
    void friendRepository_findAllByFriendId_returnMoreThenOneFriend(){
        var relation = Friend.builder()
                .userId("789")
                .friendId("123")
                .status(Status.PENDING)
                .build();

        var relation2 = Friend.builder()
                .userId("456")
                .friendId("123")
                .status(Status.PENDING)
                .build();

        friendRepository.saveAll(List.of(relation, relation2));
        var savedRelations = friendRepository.findAllByFriendId("123");

        Assertions.assertNotNull(savedRelations);
        Assertions.assertEquals(2, savedRelations.size());
    }

    @Test
    void friendRepository_findByUserIdAndFriendId_returnFriend(){
        var relation = Friend.builder()
                .userId("123")
                .friendId("456")
                .status(Status.PENDING)
                .build();

        friendRepository.save(relation);
        var savedRelation = friendRepository.findByUserIdAndFriendId("123", " 456");

        Assertions.assertNotNull(savedRelation);
    }
}