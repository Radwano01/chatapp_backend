package com.project.chatApp.user.dto;

import com.project.chatApp.friend.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindDTO {
    private String id;
    private String fullName;
    private String username;
    private String avatar;
    private Status relationStatus;
    private String senderId;
}
