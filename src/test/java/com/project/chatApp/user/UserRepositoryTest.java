package com.project.chatApp.user;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void userRepository_findByUsername_returnUserNotNull(){
        //arrange
        User user = User.builder()
                .username("username")
                .build();

        //act
        userRepository.save(user);

        User savedUser = userRepository.findByUsername("username").orElse(null);
        //assert

        Assertions.assertNotNull(savedUser);
    }
}