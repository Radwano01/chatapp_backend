package com.project.chatApp.group;

import lombok.Getter;

@Getter
public enum Roles {
    USER(1), ADMIN(2), OWNER(3);

    private final int level;

    Roles(int level) {
        this.level = level;
    }

}
