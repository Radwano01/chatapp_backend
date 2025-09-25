package com.project.chatApp.chatRoom;


import com.project.chatApp.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatrooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String chatId;

    @ManyToMany
    @JoinTable(
            name = "chatroom_members",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> members = new ArrayList<>();

    private Date createdAt;
}
