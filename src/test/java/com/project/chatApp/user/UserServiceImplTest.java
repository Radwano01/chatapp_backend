package com.project.chatApp.user;

import com.project.chatApp.config.JWTGenerator;
import com.project.chatApp.exception.BadRequestException;
import com.project.chatApp.exception.ResourceAlreadyExistsException;
import com.project.chatApp.friend.FriendService;
import com.project.chatApp.user.dto.CreateDTO;
import com.project.chatApp.user.dto.EditDTO;
import com.project.chatApp.user.dto.LoginDTO;
import com.project.chatApp.user.dto.PasswordDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    FriendService friendService;

    @Mock
    JWTGenerator jwtGenerator;

    @InjectMocks
    UserServiceImpl userService;

    private User user;

    @BeforeEach
    void init(){
        user = User.builder()
                .id("1")
                .username("username")
                .fullName("fullName")
                .avatar("avatar")
                .description("desc")
                .status(Status.OFFLINE)
                .role(Role.USER)
                .password("password")
                .build();
    }

    @Test
    void userService_save_returnUsernameException(){
        var createDTO = CreateDTO.builder()
                .username("username")
                .fullName("fullName")
                .password("password")
                .build();

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        assertThrows(ResourceAlreadyExistsException.class, ()-> userService.save(createDTO));
    }

    @Test
    void userService_save_returnNotNull(){
        var createDTO = CreateDTO.builder()
                .username("username")
                .fullName("fullName")
                .password("password")
                .build();


        when(userRepository.findByUsername(Mockito.any(String.class))).thenReturn(Optional.empty());

        userService.save(createDTO);

        verify(userRepository, times(1)).save(Mockito.any(User.class));
    }

    @Test
    void userService_edit_returnUser(){
        var editDTO = EditDTO.builder()
                .fullName("newFullName")
                .avatar("newAvatar")
                .description("newDesc")
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        var editedUser = userService.edit("1", editDTO);

        Assertions.assertNotNull(editedUser);
        Assertions.assertEquals(editedUser.getUsername(), user.getUsername());
        Assertions.assertEquals(editedUser.getAvatar(), user.getAvatar());
        Assertions.assertEquals(editedUser.getFullName(), user.getFullName());
    }

    @Test
    void userService_login_returnUserDTO(){
        LoginDTO loginDTO = LoginDTO.builder()
                .username("username")
                .password("password")
                .build();

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(jwtGenerator.generateToken(user)).thenReturn("token");

        var savedUser = userService.login(loginDTO);

        assertAll(
                ()-> assertEquals(user.getId(), savedUser.getId()),
                ()-> assertEquals(user.getUsername(), savedUser.getUsername()),
                ()-> assertEquals(user.getFullName(), savedUser.getFullName()),
                ()-> assertEquals(user.getAvatar(), savedUser.getAvatar()),
                ()-> assertEquals(Status.ONLINE, savedUser.getStatus()),
                ()-> assertEquals("token", savedUser.getToken())
        );
    }

    @Test
    void userService_logout_returnUser(){

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        var savedUser = userService.logout("1");

        assertEquals(Status.OFFLINE, savedUser.getStatus());
    }

    @Test
    void userService_delete_returnDeleted(){
        userService.delete("1");

        verify(userRepository, times(1)).deleteById("1");
    }

    @Test
    void userService_editPassword_returnEdited(){
        PasswordDTO passwordDTO = PasswordDTO.builder()
                .currentPassword("password")
                .newPassword("newPassword")
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        userService.editPassword("1", passwordDTO);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void userService_editPassword_returnBadRequestException(){
        PasswordDTO passwordDTO = PasswordDTO.builder()
                .currentPassword("pass111")
                .newPassword("newPassword")
                .build();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordDTO.getCurrentPassword(), user.getPassword())).thenReturn(true);

        assertThrows(BadRequestException.class, ()-> userService.editPassword("1", passwordDTO));
    }

    @Test
    void userService_getDetails_returnOwnUserDetails(){

        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        var savedUser = userService.getDetails("1");

        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(user.getFullName(), savedUser.getFullName());
        Assertions.assertEquals(user.getDescription(), savedUser.getDescription());
        Assertions.assertEquals(user.getStatus(), savedUser.getStatus());
    }

    @Test
    void userService_getDetails_returnUserDetails(){
        var otherUser = User.builder()
                .id("2")
                .username("otherUsername")
                .description("otherDesc")
                .status(Status.OFFLINE)
                .build();

        when(userRepository.findById("2")).thenReturn(Optional.of(otherUser));
        when(friendService.getRelationStatus("1", "2"))
                .thenReturn(com.project.chatApp.friend.Status.PENDING);
        when(friendService.getSenderId("1", "2")).thenReturn("1");

        var savedUser = userService.getDetails("1", "2");

        assertAll(
                ()-> assertEquals(otherUser.getUsername(), savedUser.getUsername()),
                ()-> assertEquals(otherUser.getDescription(), savedUser.getDescription()),
                ()-> assertEquals(otherUser.getStatus(), savedUser.getStatus()),
                ()-> assertEquals(com.project.chatApp.friend.Status.PENDING, savedUser.getRelationStatus()),
                ()-> assertEquals("1", savedUser.getSenderId())
        );
    }

    @Test
    void userService_findUser_returnUserMiniDetailsDTO(){
        User user2 = User.builder()
                .id("2")
                .username("username2")
                .avatar("avatar2")
                .build();

        when(userRepository.findByUsername("username2")).thenReturn(Optional.of(user2));
        when(friendService.getRelationStatus("1", "2")).thenReturn(com.project.chatApp.friend.Status.PENDING);
        when(friendService.getSenderId("1", "2")).thenReturn("1");

        var savedUser = userService.findUser("1", "username2");

        assertAll(
                ()-> assertEquals(user2.getId(), savedUser.getId()),
                ()-> assertEquals(user2.getUsername(), savedUser.getUsername()),
                ()-> assertEquals(user2.getAvatar(), savedUser.getAvatar()),
                ()-> assertEquals(com.project.chatApp.friend.Status.PENDING, savedUser.getRelationStatus()),
                ()-> assertEquals("1", savedUser.getSenderId())
        );
    }
}