package com.project.chatApp.chatRoom;

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

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatRoomController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class ChatRoomControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ChatRoomService chatRoomService;

    @Test
    void chatRoomController_createPrivateChatRoom_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(post("/api/v1/chatrooms/users/2")
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(chatRoomService).getOrCreatePrivateRoom("1", "2");
    }

    @Test
    void chatRoomController_getUserChatRooms_returnChatRoomDTOs() throws Exception{

        ResultActions response = mockMvc.perform(get("/api/v1/chatrooms")
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(chatRoomService).getUserChatRooms("1");
    }
}