package com.project.chatApp.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatApp.config.JWTAuthenticationFilter;
import com.project.chatApp.user.UserController;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ChatMessageRestController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class ChatMessageRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    ChatMessageService chatMessageService;

    @Test
    void chatMessageController_getMessages_returnChatMessageDTOs() throws Exception{

        ResultActions response = mockMvc.perform(get("/api/v1/messages/1"));

        response.andExpect(status().isOk());

        verify(chatMessageService).getChatMessages("1");
    }
}