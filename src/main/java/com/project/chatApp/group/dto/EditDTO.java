package com.project.chatApp.group.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EditDTO {
    private String name;
    private String description;
    private String avatar;
}
