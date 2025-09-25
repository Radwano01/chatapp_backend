package com.project.chatApp.friend;


import com.project.chatApp.user.dto.FindDTO;

import java.util.List;

public interface FriendService {

    void add(String userId, String friendId);

    void changeStatus(String userId, String friendId, Status status);

    void removeFriend(String userId, String friendId);

    List<FindDTO> getFriends(String userId);

    Status getRelationStatus(String userId, String otherUserId);

    String getSenderId(String userId, String otherUserId);
}
