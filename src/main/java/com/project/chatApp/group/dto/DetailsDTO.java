package com.project.chatApp.group.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@Builder
public class DetailsDTO {
    private String description;
    private List<GetUserDetailsDTO> members;
    private Date createdAt;
}



