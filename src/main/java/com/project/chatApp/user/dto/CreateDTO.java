package com.project.chatApp.user.dto;

import lombok.Builder;
import lombok.Data;
import com.project.chatApp.user.Status;


@Data
@Builder
public class CreateDTO {
    private String username;
    private String fullName;
    private String password;
}
