package com.project.chatApp.user;


import jakarta.persistence.*;
import lombok.*;


import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    private String fullName;
    private String avatar;
    private String description;
    private String password;
    private Status status;
    private Role role;

    @Builder.Default
    private Date timestamp = new Date();
}
