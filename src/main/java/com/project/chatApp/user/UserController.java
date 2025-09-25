package com.project.chatApp.user;

import com.project.chatApp.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserAndTokenDTO> login(@RequestBody LoginDTO loginDTO){
        return ResponseEntity.ok(userService.login(loginDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal){
        userService.logout(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> save(@RequestBody CreateDTO createDTO){
        userService.save(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<User> edit(@RequestBody EditDTO editDTO,
                                     Principal principal){
        return ResponseEntity.ok().body(userService.edit(principal.getName(), editDTO));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> edit(@RequestBody PasswordDTO passwordDTO,
                                     Principal principal){
        userService.editPassword(principal.getName(), passwordDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(Principal principal){
        userService.delete(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/details")
    public ResponseEntity<GetOwnDetailsDTO> details(Principal principal){
        return ResponseEntity.ok(userService.getDetails(principal.getName()));
    }

    @GetMapping("/{otherUserId}/details")
    public ResponseEntity<GetDetailsDTO> userDetails(@PathVariable("otherUserId") String otherUserId,
                                                     Principal principal){
        return ResponseEntity.ok(userService.getDetails(principal.getName(), otherUserId));
    }

    @GetMapping("/{username}")
    public ResponseEntity<GetMiniDetailsDTO> findUser(@PathVariable("username") String username,
                                                      Principal principal) {
        return ResponseEntity.ok(userService.findUser(principal.getName(), username));
    }
}
