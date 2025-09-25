package com.project.chatApp.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Integer> {

    @Query("select gm.group from GroupMember gm where gm.user.id = :userId")
    List<Group> findAllGroupsByUserId(@Param("userId") String userId);
}
