package com.project.chatApp.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordDTO {
    private String currentPassword;
    private String newPassword;
}
