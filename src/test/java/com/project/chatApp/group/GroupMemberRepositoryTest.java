package com.project.chatApp.group;


import com.project.chatApp.user.Role;
import com.project.chatApp.user.Status;
import com.project.chatApp.user.User;
import com.project.chatApp.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class GroupMemberRepositoryTest {

    @Autowired
    GroupMemberRepository groupMemberRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Test
    void groupMemberRepository_findAllGroupsByUserId_returnsMoreThenOneGroup(){
        var group = Group.builder()
                .name("name")
                .description("description")
                .avatar("avatar")
                .chatId("chatId")
                .build();

        var user = User.builder()
                .username("username")
                .fullName("fullname")
                .status(Status.ONLINE)
                .password("password")
                .avatar("avatar")
                .description("description")
                .role(Role.USER)
                .build();

        var groupMember = GroupMember.builder()
                .group(group)
                .user(user)
                .build();


        userRepository.save(user);
        groupRepository.save(group);
        groupMemberRepository.save(groupMember);

        List<Group> groups = groupMemberRepository.findAllGroupsByUserId(user.getId());

        Assertions.assertNotNull(groups);
        Assertions.assertEquals(1, groups.size());
    }
}