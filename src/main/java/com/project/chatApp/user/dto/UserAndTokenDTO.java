package com.project.chatApp.user.dto;
import com.project.chatApp.user.Status;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserAndTokenDTO {
    private String id;
    private String username;
    private String fullName;
    private String avatar;
    private Status status;
    private String token;
}
