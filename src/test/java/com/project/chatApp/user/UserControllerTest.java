package com.project.chatApp.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatApp.config.JWTAuthenticationFilter;
import com.project.chatApp.user.dto.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void userController_save_returnCreated() throws Exception {
        CreateDTO createDTO = CreateDTO.builder()
                .fullName("fullName")
                .username("username")
                .password("password")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)));

        response.andExpect(status().isCreated());

        verify(userService).save(any(CreateDTO.class));
    }

    @Test
    void userController_logout_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(post("/api/v1/users/logout")
                .principal(() -> "1"));

        response.andExpect(status().isNoContent());

        verify(userService).logout("1");
    }

    @Test
    void userController_login_returnUser() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder()
                .username("username")
                .password("password")
                .build();

        UserAndTokenDTO userAndTokenDTO = UserAndTokenDTO.builder()
                .id("1")
                .username("username")
                .fullName("fullName")
                .avatar("avatar")
                .status(Status.OFFLINE)
                .token("token")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO))
        );

        response.andExpect(status().isOk());

        verify(userService).login(loginDTO);
    }

    @Test
    void userController_edit_detailsCase_returnUser() throws Exception {
        EditDTO editDTO = EditDTO.builder()
                .fullName("fullName1")
                .description("desc1")
                .avatar("avatar1")
                .build();

        ResultActions response = mockMvc.perform(put("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO))
                .principal(()->"1"));

        response.andExpect(status().isOk());

        verify(userService).edit("1", editDTO);
    }

    @Test
    void userController_edit_passwordCase_returnOk() throws Exception {
        PasswordDTO newPassword = PasswordDTO.builder().newPassword("newPassword").build();

        ResultActions response = mockMvc.perform(put("/api/v1/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPassword))
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(userService).editPassword("1", newPassword);
    }

    @Test
    void userController_delete_returnNoContent() throws Exception {
        ResultActions response = mockMvc.perform(delete("/api/v1/users")
                .principal(()->"1")
        );

        response.andExpect(status().isNoContent());

        verify(userService).delete("1");
    }

    @Test
    void userController_userDetails_returnUserDetailsDTOs() throws Exception {

        ResultActions response = mockMvc.perform(get("/api/v1/users/details")
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(userService).getDetails("1");
    }

    @Test
    void userController_userDetails_otherUserCase_returnDetailsDTO() throws Exception{

        ResultActions response = mockMvc.perform(get("/api/v1/users/2/details")
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(userService).getDetails("1","2");
    }

    @Test
    void userController_findUser_return_returnMiniUserDetails() throws Exception {

        ResultActions response = mockMvc.perform(get("/api/v1/users/username")
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(userService).findUser("1", "username");
    }
}
