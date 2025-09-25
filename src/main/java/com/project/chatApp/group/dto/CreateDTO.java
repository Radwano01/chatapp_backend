package com.project.chatApp.group.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDTO {
    private String name;
    private String description;
    private String avatar;
}
