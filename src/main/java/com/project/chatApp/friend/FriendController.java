package com.project.chatApp.friend;

import com.project.chatApp.user.dto.FindDTO;
import com.project.chatApp.user.dto.GetMiniDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable("friendId") String friendId,
                                          Principal principal){
        friendService.add(principal.getName(), friendId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<Void> changeStatus(@PathVariable("friendId") String friendId,
                                             @RequestParam("status") Status status,
                                             Principal principal){
        friendService.changeStatus(principal.getName(), friendId, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable("friendId") String friendId,
                                             Principal principal){
        friendService.removeFriend(principal.getName(), friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<FindDTO>> getFriends(Principal principal){
        return ResponseEntity.ok(friendService.getFriends(principal.getName()));
    }
}

