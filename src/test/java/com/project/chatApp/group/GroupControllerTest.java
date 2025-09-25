package com.project.chatApp.group;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatApp.config.JWTAuthenticationFilter;
import com.project.chatApp.group.dto.CreateDTO;
import com.project.chatApp.group.dto.EditDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = GroupController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthenticationFilter.class)
})
@AutoConfigureMockMvc(addFilters = false)
class GroupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    GroupService groupService;

    @Test
    void groupController_save_returnCreated() throws Exception{
        CreateDTO createDTO = CreateDTO.builder()
                .name("name")
                .avatar("avatar")
                .description("desc")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO))
                .principal(()->"1")
        );

        response.andExpect(status().isCreated());

        verify(groupService).save(any(CreateDTO.class), eq("1"));
    }

    @Test
    void groupController_edit_returnGroupDTO() throws Exception{
        EditDTO editDTO = EditDTO.builder()
                .name("name1")
                .description("desc1")
                .avatar("avatar1")
                .build();

        ResultActions response = mockMvc.perform(put("/api/v1/groups/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO))
                .principal(()->"1")
        );

        response.andExpect(status().isOk());

        verify(groupService).edit(eq(1), eq("1"), any(EditDTO.class));
    }

    @Test
    void groupController_delete_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(delete("/api/v1/groups/1")
                .principal(()->"1")
        );

        response.andExpect(status().isNoContent());

        verify(groupService).delete(1, "1");
    }

    @Test
    void groupController_changeUserRole_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(put("/api/v1/groups/1/users/2/role")
                .principal(()->"1")
                .param("role", "OWNER")
        );

        response.andExpect(status().isNoContent());

        verify(groupService).changeUserRole(eq(1), eq("1"), eq("2"), any(Roles.class));
    }

    @Test
    void groupController_removeUser_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(delete("/api/v1/groups/1/users/2/remove")
                .principal(()-> "1")
        );

        response.andExpect(status().isNoContent());

        verify(groupService).removeUser(1, "1", "2");
    }

    @Test
    void groupController_leaveGroup_returnNoContent() throws Exception{

        ResultActions response = mockMvc.perform(delete("/api/v1/groups/1/leave")
                .principal(()->"1")
        );

        response.andExpect(status().isNoContent());

        verify(groupService).leaveGroup(1, "1");
    }

    @Test
    void groupController_getUserGroups_returnUserGroupDTOs() throws Exception{

        ResultActions response = mockMvc.perform(get("/api/v1/groups")
                .principal(()-> "1"));

        response.andExpect(status().isOk());

        verify(groupService).userGroups("1");
    }

    @Test
    void groupController_getGroupDetails_returnGroupDetailsDTO() throws Exception{

        ResultActions response = mockMvc.perform(get("/api/v1/groups/1/details"));

        response.andExpect(status().isOk());

        verify(groupService).groupDetails(1);
    }
}