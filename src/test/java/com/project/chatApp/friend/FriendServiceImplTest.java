package com.project.chatApp.friend;


import com.project.chatApp.exception.BadRequestException;
import com.project.chatApp.exception.ResourceAlreadyExistsException;
import com.project.chatApp.user.Role;
import com.project.chatApp.user.Status;
import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import com.project.chatApp.user.dto.FindDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {

    @Mock
    FriendRepository friendRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    FriendServiceImpl friendService;

    private User user;

    private User user2;

    private Friend friend;

    @BeforeEach
    void init(){
        user = User.builder()
                .id("1")
                .username("username")
                .fullName("Full Name")
                .description("desc")
                .avatar("avatar")
                .status(com.project.chatApp.user.Status.OFFLINE)
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

        friend = Friend.builder()
                .id(1)
                .userId(user.getId())
                .friendId(user2.getId())
                .status(com.project.chatApp.friend.Status.PENDING)
                .build();
    }

    @Test
    void friendService_add_returnNotNull(){
        when(friendRepository.existsByUserIdAndFriendId("3", "4")).thenReturn(Optional.of(false));

        friendService.add("3", "4");

        verify(friendRepository, times(1)).save(Mockito.any(Friend.class));
    }

    @Test
    void friendService_add_returnBadRequestException(){
        assertThrows(BadRequestException.class, ()-> friendService.add("3", "3"));
    }

    @Test
    void friendService_add_returnAlreadyExistException(){
        when(friendRepository.existsByUserIdAndFriendId("1", "2")).thenReturn(Optional.of(true));

        assertThrows(ResourceAlreadyExistsException.class, ()-> friendService.add("1", "2"));
    }

    @Test
    void friendService_changeStatus_returnNotNull(){
        when(friendRepository.findByUserIdAndFriendId("1", "2")).thenReturn(Optional.of(friend));

        friendService.changeStatus("1", "2", com.project.chatApp.friend.Status.ACCEPTED);

        verify(friendRepository, times(1)).save(friend);
    }

    @Test
    void friendService_changeStatus_returnDeleted(){
        when(friendRepository.findByUserIdAndFriendId("2", "1")).thenReturn(Optional.of(friend));

        friendService.changeStatus("2", "1", com.project.chatApp.friend.Status.DECLINED);

        verify(friendRepository, times(1)).delete(friend);
    }

    @Test
    void friendService_removeFriend_returnDeleted(){
        friendService.removeFriend("1", "2");

        verify(friendRepository, times(1)).deleteByUserIdAndFriendId("1", "2");
        verify(friendRepository, times(1)).deleteByUserIdAndFriendId("2", "1");
    }

    @Test
    void friendService_getFriends_returnFriendDTO(){
        when(friendRepository.findAllByUserId("1")).thenReturn(List.of(friend));
        when(userRepository.findAllById(Set.of("2"))).thenReturn(List.of(user2));
        when(friendRepository.findByUserIdAndFriendId("1", "2")).thenReturn(Optional.of(friend));

        List<FindDTO> friends = friendService.getFriends("1");

        assertAll(
                () -> assertEquals(user2.getFullName(), friends.get(0).getFullName()),
                ()-> assertEquals(user2.getUsername(), friends.get(0).getUsername()),
                () -> assertEquals(user2.getAvatar(), friends.get(0).getAvatar()),
                () -> assertEquals(friend.getStatus(), friends.get(0).getRelationStatus()),
                () -> assertEquals("1", friends.get(0).getSenderId())
        );
    }

    @Test
    void friendService_getRelationsStatus_returnStatus(){
        when(friendRepository.findByUserIdAndFriendId("1", "2")).thenReturn(Optional.of(friend));

        var savedStatus = friendService.getRelationStatus("1", "2");

        Assertions.assertEquals(friend.getStatus(), savedStatus);
    }
}