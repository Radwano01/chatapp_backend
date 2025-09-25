package com.project.chatApp.chatRoom.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetChatRoomDTO {
    private int id;
    private String otherUserId;
    private String chatId;
    private String fullName;
    private String avatar;
}
