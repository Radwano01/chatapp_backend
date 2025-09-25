package com.project.chatApp.user.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditDTO {
    private String fullName;
    private String avatar;
    private String description;
}
