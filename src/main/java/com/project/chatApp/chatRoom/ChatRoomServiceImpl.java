package com.project.chatApp.chatRoom;

import com.project.chatApp.chatRoom.dto.GetChatRoomDTO;

import com.project.chatApp.exception.ResourceNotFoundException;
import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    public ChatRoom getOrCreatePrivateRoom(String senderId, String recipientId) {
        String chatId = buildPrivateChatId(senderId, recipientId);

        List<User> users = userRepository.findAllById(List.of(senderId, recipientId));

        if (users.size() != 2) {
            throw new ResourceNotFoundException("One or both users not found");
        }

        return chatRoomRepository.findChatRoomByChatId(chatId)
                .orElseGet(() -> {
                    Date now = new Date();
                    ChatRoom room = ChatRoom.builder()
                            .chatId(chatId)
                            .members(users)
                            .createdAt(now)
                            .build();
                    return chatRoomRepository.save(room);
                });
    }

    @Override
    public List<GetChatRoomDTO> getUserChatRooms(String userId) {
        List<ChatRoom> rooms = chatRoomRepository.findAllByMember(userId);

        List<GetChatRoomDTO> chatRoomsDetails = new ArrayList<>();

        for (ChatRoom room : rooms) {
            if (room.getMembers().size() != 2) continue;

            String otherUserId = room.getMembers().get(0).getId().equals(userId) ?
                    room.getMembers().get(1).getId() :
                    room.getMembers().get(0).getId();

            User recipientDetails = userRepository.findById(otherUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

            chatRoomsDetails.add(
                    GetChatRoomDTO.builder()
                            .id(room.getId())
                            .otherUserId(otherUserId)
                            .chatId(room.getChatId())
                            .fullName(recipientDetails.getFullName())
                            .avatar(recipientDetails.getAvatar())
                            .build()
            );
        }

        return chatRoomsDetails;
    }

    @Override
    @Transactional
    public String getRecipientId(String chatId, String senderId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByChatId(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom not found"));

        String member1 = chatRoom.getMembers().get(0).getId();
        String member2 = chatRoom.getMembers().get(1).getId();

        return member1.equals(senderId) ? member2 : member1;
    }

    private String buildPrivateChatId(String userA, String userB) {
        if (userA == null || userB == null) {
            throw new IllegalArgumentException("Both user IDs must be provided to build chat ID");
        }
        return userA.compareTo(userB) < 0 ? userA + "_" + userB : userB + "_" + userA;
    }
}
