package com.project.chatApp.chatRoom;


import com.project.chatApp.chatRoom.dto.GetChatRoomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/chatrooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/users/{recipientId}")
    public ResponseEntity<Void> createPrivateChatRoom(@PathVariable("recipientId") String recipientId,
                                                      Principal principal){
        chatRoomService.getOrCreatePrivateRoom(principal.getName(), recipientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GetChatRoomDTO>> getUserChatRooms(Principal principal){
        return ResponseEntity.ok(chatRoomService.getUserChatRooms(principal.getName()));
    }
}
