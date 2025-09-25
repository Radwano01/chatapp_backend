package com.project.chatApp.user;

import com.project.chatApp.user.dto.*;

public interface UserService {

    void save(CreateDTO createDTO);

    User edit(String userId, EditDTO editDTO);

    void delete(String userId);

    UserAndTokenDTO login(LoginDTO loginDTO);

    User logout(String userId);

    void editPassword(String userId, PasswordDTO passwordDTO);

    GetOwnDetailsDTO getDetails(String userId);

    GetDetailsDTO getDetails(String userId, String otherUserId);

    GetMiniDetailsDTO findUser(String userId, String username);
}
