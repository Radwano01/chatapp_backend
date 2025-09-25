package com.project.chatApp.user.dto;

import com.project.chatApp.user.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetDetailsDTO {
    private String username;
    private String description;
    private Status status;
    private com.project.chatApp.friend.Status relationStatus;
    private String senderId;
}
