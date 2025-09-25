package com.project.chatApp.chatRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query("select cr from ChatRoom cr join cr.members where cr.chatId = :chatId")
    Optional<ChatRoom> findChatRoomByChatId(@Param("chatId") String chatId);

    @Query("select cr from ChatRoom cr join cr.members m where m.id = :userId")
    List<ChatRoom> findAllByMember(@Param("userId") String userId);
}
