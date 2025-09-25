package com.project.chatApp.friend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatApp.config.JWTAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FriendController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    FriendService friendService;


    @Test
    void friendController_addFriend_returnCreated() throws Exception{

        ResultActions response = mockMvc.perform(post("/api/v1/friends/2")
                .principal(()->"1")
        );

        response.andExpect(status().isCreated());

        verify(friendService).add("1", "2");
    }

    @Test
    void friendController_changeStatus_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(put("/api/v1/friends/2")
                .principal(()->"1")
                .param("status", "ACCEPTED")
        );

        response.andExpect(status().isNoContent());

        verify(friendService).changeStatus(eq("1"), eq("2"), any(Status.class));
    }

    @Test
    void friendController_removeFriend_returnCreated() throws Exception{

        ResultActions response = mockMvc.perform(delete("/api/v1/friends/2")
                .principal(()->"1")
        );

        response.andExpect(status().isNoContent());

        verify(friendService).removeFriend("1", "2");
    }

    @Test
    void friendController_getFriends_returnFriendDTOs() throws Exception{

        ResultActions response = mockMvc.perform(get("/api/v1/friends")
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(friendService).getFriends("1");
    }
}