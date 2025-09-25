package com.project.chatApp.group;


import com.project.chatApp.group.dto.CreateDTO;
import com.project.chatApp.group.dto.DetailsDTO;
import com.project.chatApp.group.dto.EditDTO;
import com.project.chatApp.group.dto.GetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;


    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CreateDTO createDTO, Principal principal){
        groupService.save(createDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Group> edit(@PathVariable("groupId") int groupId,
                                      @RequestBody EditDTO editDTO,
                                      Principal principal){
        return ResponseEntity.ok(groupService.edit(groupId, principal.getName(), editDTO));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(@PathVariable("groupId") int groupId,
                                       Principal principal){
        groupService.delete(groupId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}/users/{userId}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable("groupId") int groupId,
                                               @PathVariable("userId") String userId,
                                               @RequestParam("role") Roles role,
                                               Principal principal){
        groupService.changeUserRole(groupId, principal.getName(), userId, role);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}/users/{username}")
    public ResponseEntity<Void> addUser(@PathVariable("groupId") int groupId,
                                        @PathVariable("username") String username){
        groupService.addUser(groupId, username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/users/{userId}/remove")
    public ResponseEntity<Void> removeUser(@PathVariable("groupId") int groupId,
                                           @PathVariable("userId") String userId,
                                            Principal principal){
        groupService.removeUser(groupId, principal.getName(), userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable("groupId") int groupId, Principal principal) {
        groupService.leaveGroup(groupId, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GetDTO>> getUserGroups(Principal principal){
        return ResponseEntity.ok(groupService.userGroups(principal.getName()));
    }

    @GetMapping("/{groupId}/details")
    public ResponseEntity<DetailsDTO> getGroupDetails(@PathVariable("groupId") int groupId){
        return ResponseEntity.ok(groupService.groupDetails(groupId));
    }
}
