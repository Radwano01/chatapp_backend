package com.project.chatApp.group.dto;

import com.project.chatApp.group.Roles;
import com.project.chatApp.user.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserDetailsDTO {
    private String id;
    private String username;
    private String fullName;
    private String avatar;
    private String description;
    private Status status;
    private Roles role;
}
