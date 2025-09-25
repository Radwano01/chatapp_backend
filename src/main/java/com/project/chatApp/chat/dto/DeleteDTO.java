package com.project.chatApp.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteDTO {
    private String messageId;
}
