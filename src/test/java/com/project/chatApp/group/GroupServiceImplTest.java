package com.project.chatApp.group;

import com.project.chatApp.exception.BadRequestException;
import com.project.chatApp.exception.ResourceAlreadyExistsException;
import com.project.chatApp.exception.ResourceDoesNotHaveAccessException;
import com.project.chatApp.exception.ResourceNotFoundException;
import com.project.chatApp.group.dto.CreateDTO;
import com.project.chatApp.group.dto.EditDTO;
import com.project.chatApp.user.Role;
import com.project.chatApp.user.Status;
import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    private User user;

    private User user2;

    private Group group;

    @BeforeEach
    void init() {
        user = User.builder()
                .id("1")
                .username("username")
                .fullName("Full Name")
                .description("desc")
                .avatar("avatar")
                .status(Status.OFFLINE)
                .role(Role.USER)
                .build();

        user2 = User.builder()
                .id("2")
                .username("username2")
                .fullName("Full Name2")
                .description("desc2")
                .avatar("avatar2")
                .status(Status.OFFLINE)
                .role(Role.USER)
                .build();

        group = Group.builder()
                .id(1)
                .chatId("chatId")
                .name("name")
                .description("desc")
                .avatar("avatar")
                .members(new ArrayList<>())
                .build();

        GroupMember member = GroupMember.builder()
                .user(user)
                .group(group)
                .role(Roles.OWNER)
                .build();

        GroupMember member2 = GroupMember.builder()
                .group(group)
                .user(user2)
                .role(Roles.USER)
                .build();
        group.getMembers().add(member);
        group.getMembers().add(member2);
    }

    @Test
    void groupService_saveGroup_returnNotNull() {
        CreateDTO createDTO = CreateDTO.builder()
                .name("Test Group")
                .description("Group description")
                .avatar("avatar.png")
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        groupService.save(createDTO, user.getId());

        verify(groupRepository, times(1)).save(Mockito.any(Group.class));
    }

    @Test
    void groupService_saveGroup_returnException() {
        CreateDTO createDTO = CreateDTO.builder().name("Group").build();

        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.save(createDTO, "1"));
    }

    @Test
    void groupService_editGroup_returnGroup() {
        EditDTO editDTO = EditDTO.builder()
                .name("New Group Name")
                .description("New Description")
                .avatar("newAvatar.png")
                .build();


        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(groupRepository.save(Mockito.any(Group.class))).thenReturn(group);

        var editedGroup = groupService.edit(1, "1", editDTO);

        Assertions.assertNotNull(editedGroup);
        Assertions.assertEquals(group.getName(), editedGroup.getName());
        Assertions.assertEquals(group.getDescription(), editedGroup.getDescription());
        Assertions.assertEquals(group.getAvatar(), editedGroup.getAvatar());
    }

    @Test
    void groupService_deleteGroup_returnDeleted() {
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        groupService.delete(1, "1");

        verify(groupRepository, times(1)).deleteById(1);
    }

    @Test
    void groupService_deleteGroup_returnOwnerException(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertThrows(BadRequestException.class, ()-> groupService.delete(1, "2"));
    }

    @Test
    void groupService_changeUserRole_returnNotNull(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        groupService.changeUserRole(1, "1", "2", Roles.ADMIN);

        GroupMember updatedMember = group.getMembers().stream().filter(
                m-> m.getUser().getId().equals("2")
        ).findFirst().orElseThrow();

        Assertions.assertEquals(Roles.ADMIN, updatedMember.getRole());
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void groupService_changeUserRole_returnRoleException(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertThrows(ResourceAlreadyExistsException.class, ()-> groupService.changeUserRole(1, "2", "1", Roles.ADMIN));
    }

    @Test
    void groupService_changeUserRole_returnNotFoundUserException(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertThrows(ResourceNotFoundException.class, ()-> groupService.changeUserRole(1, "2", "3", Roles.ADMIN));
    }

    @Test
    void groupService_addUser_returnNotNull(){
        var user3 = User.builder()
                .id("3")
                .username("username3")
                .build();

        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername(Mockito.any(String.class))).thenReturn(Optional.of(user3));

        groupService.addUser(1, "3");

        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void groupService_addUser_returnExistException(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(userRepository.findByUsername("username2")).thenReturn(Optional.of(user2));

        assertThrows(ResourceAlreadyExistsException.class, ()-> groupService.addUser(1, "username2"));
    }

    @Test
    void groupService_removeUser_returnNotNull(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        groupService.removeUser(1, "1", "2");

        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void groupService_removeUser_returnAccessException(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertThrows(ResourceDoesNotHaveAccessException.class, ()-> groupService.removeUser(1, "2", "1"));
    }

    @Test
    void groupService_removeUser_returnNotFoundMember(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        assertThrows(ResourceNotFoundException.class, ()-> groupService.removeUser(1, "1", "3"));
    }

    @Test
    void groupService_leaveGroup_returnNotNull(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        groupService.leaveGroup(1, "2");

        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void groupService_leaveGroup_OwnerLeaveCase_returnDeleted(){
        Group soloGroup = Group.builder()
                .id(2)
                .name("Solo Group")
                .members(new ArrayList<>())
                .build();

        GroupMember ownerOnly = GroupMember.builder()
                .user(user)
                .group(soloGroup)
                .role(Roles.OWNER)
                .build();
        soloGroup.getMembers().add(ownerOnly);

        when(groupRepository.findById(2)).thenReturn(Optional.of(soloGroup));

        groupService.leaveGroup(2, "1");

        verify(groupRepository, times(1)).deleteById(2);
    }

    @Test
    void groupService_leaveGroup_TransferOwner_returnNotNull(){
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        groupService.leaveGroup(1, "1");

        GroupMember newOwner = group.getMembers().stream()
                .filter(m -> m.getUser().getId().equals("2"))
                .findFirst()
                .orElseThrow();

        verify(groupRepository, times(1)).save(any(Group.class));
        Assertions.assertEquals(Roles.OWNER, newOwner.getRole());
    }

    @Test
    void groupService_userGroups_returnGroupDTO(){
        when(groupMemberRepository.findAllGroupsByUserId("1")).thenReturn(List.of(group));

        var savedUserGroups = groupService.userGroups("1");

        assertAll(
                () -> Assertions.assertEquals(1, savedUserGroups.size()),
                () -> Assertions.assertEquals(group.getId(), savedUserGroups.get(0).getId()),
                () -> Assertions.assertEquals(group.getName(), savedUserGroups.get(0).getName()),
                () -> Assertions.assertEquals(group.getAvatar(), savedUserGroups.get(0).getAvatar()),
                () -> Assertions.assertEquals(Roles.OWNER, savedUserGroups.get(0).getRole())
        );
    }

    @Test
    void groupService_userGroups_returnGroupDetailsDTO(){
        Date now = new Date();
        group.setCreatedAt(now);
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));

        var savedGroupDetails = groupService.groupDetails(1);

        assertAll(
                () -> assertEquals("desc", savedGroupDetails.getDescription()),
                () -> assertEquals(now, savedGroupDetails.getCreatedAt()),
                () -> assertEquals(2, savedGroupDetails.getMembers().size()),

                // user1 (OWNER)
                () -> assertEquals("1", savedGroupDetails.getMembers().get(0).getId()),
                () -> assertEquals("username", savedGroupDetails.getMembers().get(0).getUsername()),
                () -> assertEquals("Full Name", savedGroupDetails.getMembers().get(0).getFullName()),
                () -> assertEquals(Roles.OWNER, savedGroupDetails.getMembers().get(0).getRole()),

                // user2 (USER)
                () -> assertEquals("2", savedGroupDetails.getMembers().get(1).getId()),
                () -> assertEquals("username2", savedGroupDetails.getMembers().get(1).getUsername()),
                () -> assertEquals("Full Name2", savedGroupDetails.getMembers().get(1).getFullName()),
                () -> assertEquals(Roles.USER, savedGroupDetails.getMembers().get(1).getRole())
        );
    }
}
