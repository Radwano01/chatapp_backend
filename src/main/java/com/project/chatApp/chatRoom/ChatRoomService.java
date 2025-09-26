package com.project.chatApp.chatRoom;


import com.project.chatApp.chatRoom.dto.GetChatRoomDTO;

import java.util.List;

public interface ChatRoomService {

    String getOrCreatePrivateRoom(String senderId, String recipientId);

    List<GetChatRoomDTO> getUserChatRooms(String userId);

    String getRecipientId(String chatId, String senderId);
}
