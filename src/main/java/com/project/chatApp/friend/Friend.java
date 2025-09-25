package com.project.chatApp.friend;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private String userId;

    private String friendId;

    @Enumerated(EnumType.STRING)
    private Status status;
}
