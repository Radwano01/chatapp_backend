package com.project.chatApp.user.dto;

import com.project.chatApp.user.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOwnDetailsDTO {
    private String fullName;
    private String description;
    private Status status;
}
