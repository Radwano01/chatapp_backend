package com.project.chatApp.friend;

import com.project.chatApp.exception.BadRequestException;
import com.project.chatApp.exception.ResourceAlreadyExistsException;
import com.project.chatApp.exception.ResourceNotFoundException;
import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import com.project.chatApp.user.dto.FindDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    @Override
    public void add(String userId, String friendId) {
        if(userId.equals(friendId)) throw new BadRequestException("you can't add yourself pussy");

        boolean existed = friendRepository.existsByUserIdAndFriendId(userId, friendId)
                .or(() -> friendRepository.existsByUserIdAndFriendId(friendId, userId))
                .orElse(false);

        if (existed) {
            throw new ResourceAlreadyExistsException("You are already friends with this user");
        }

        create(userId, friendId);
    }

    @Override
    public void changeStatus(String userId, String friendId, Status status) {
        var relation = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .or(() ->friendRepository.findByUserIdAndFriendId(friendId, userId))
                .orElseThrow(() -> new ResourceNotFoundException("relation not found between provided users"));

        if (status.equals(Status.DECLINED)) {
            friendRepository.delete(relation);
            return;
        }

        relation.setStatus(Status.ACCEPTED);
        friendRepository.save(relation);
    }

    @Override
    @Transactional
    public void removeFriend(String userId, String friendId) {
        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendRepository.deleteByUserIdAndFriendId(friendId, userId);
    }

    @Override
    public List<FindDTO> getFriends(String userId) {
        List<Friend> friends = Stream.concat(
                friendRepository.findAllByUserId(userId).stream(),
                friendRepository.findAllByFriendId(userId).stream()
        ).toList();

        Set<String> friendIds = friends.stream()
                .map(f -> f.getUserId().equals(userId) ? f.getFriendId() : f.getUserId())
                .collect(Collectors.toSet());

        Map<String, User> usersMap = userRepository.findAllById(friendIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return friends.stream().map(f -> {
            String friendId = f.getUserId().equals(userId) ? f.getFriendId() : f.getUserId();

            User savedUser = usersMap.get(friendId);

            return FindDTO.builder()
                    .id(savedUser.getId())
                    .fullName(savedUser.getFullName())
                    .username(savedUser.getUsername())
                    .avatar(savedUser.getAvatar())
                    .relationStatus(getRelationStatus(userId, savedUser.getId()))
                    .senderId(getSenderId(userId, savedUser.getId()))
                    .build();
        }).toList();
    }

    @Override
    public Status getRelationStatus(String userId, String otherUserId) {

        var relation = friendRepository.findByUserIdAndFriendId(userId, otherUserId)
                .or(() -> friendRepository.findByUserIdAndFriendId(otherUserId, userId))
                .orElse(null);

        return relation == null ? Status.NONE : relation.getStatus();
    }

    @Override
    public String getSenderId(String userId, String otherUserId){
        return friendRepository.findByUserIdAndFriendId(userId, otherUserId)
                .map(Friend::getUserId)
                .or(()-> friendRepository.findByUserIdAndFriendId(otherUserId, userId)
                        .map(Friend::getUserId))
                .orElse(null);
    }

    private void create(String userId, String friendId) {
        var newFriend = Friend.builder()
                .userId(userId)
                .friendId(friendId)
                .status(Status.PENDING)
                .build();
        friendRepository.save(newFriend);
    }
}
