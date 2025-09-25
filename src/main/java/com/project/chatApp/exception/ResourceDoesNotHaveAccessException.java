package com.project.chatApp.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceDoesNotHaveAccessException extends RuntimeException {
    public ResourceDoesNotHaveAccessException(String message) {
        super(message);
    }
}
