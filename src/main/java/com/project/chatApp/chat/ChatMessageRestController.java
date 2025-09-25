package com.project.chatApp.chat;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class ChatMessageRestController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable("chatId") String chatId){
        return ResponseEntity.ok(chatMessageService.getChatMessages(chatId));
    }
}
