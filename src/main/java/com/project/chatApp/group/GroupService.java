package com.project.chatApp.group;

import com.project.chatApp.group.dto.CreateDTO;
import com.project.chatApp.group.dto.DetailsDTO;
import com.project.chatApp.group.dto.EditDTO;
import com.project.chatApp.group.dto.GetDTO;

import java.util.List;

public interface GroupService {

    void save(CreateDTO createDTO, String userId);

    Group edit(int groupId, String ownerId, EditDTO editDTO);

    void delete(int groupId, String userId);

    void changeUserRole(int groupId, String modId, String userId, Roles role);

    void addUser(int groupId, String username);

    void removeUser(int groupId, String modId, String userId);

    void leaveGroup(int groupId, String userId);

    List<GetDTO> userGroups(String userId);

    DetailsDTO groupDetails(int groupId);
}
