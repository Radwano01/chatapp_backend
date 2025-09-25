package com.project.chatApp.friend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {
    List<Friend> findAllByUserId(String userId);

    Optional<Boolean> existsByUserIdAndFriendId(String userId, String friendId);

    void deleteByUserIdAndFriendId(String userId, String friendId);

    List<Friend> findAllByFriendId(String userId);

    @Query("select f from Friend f where f.userId = :userId and f.friendId = :friendId")
    Optional<Friend> findByUserIdAndFriendId(@Param("userId") String userId, @Param("friendId") String friendId);
}
