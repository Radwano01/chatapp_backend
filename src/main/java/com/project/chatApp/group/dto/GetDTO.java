package com.project.chatApp.group.dto;

import com.project.chatApp.group.Roles;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetDTO {
    private int id;
    private String name;
    private Roles role;
    private String avatar;
}
