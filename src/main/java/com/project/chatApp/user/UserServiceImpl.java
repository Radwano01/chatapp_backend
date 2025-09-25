package com.project.chatApp.user;


import com.project.chatApp.config.JWTGenerator;
import com.project.chatApp.exception.BadRequestException;
import com.project.chatApp.exception.ResourceAlreadyExistsException;
import com.project.chatApp.exception.ResourceNotFoundException;
import com.project.chatApp.friend.FriendService;
import com.project.chatApp.user.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final FriendService friendService;


    @Override
    public void save(CreateDTO createDTO) {
        userRepository.findByUsername(createDTO.getUsername())
                .ifPresent(u -> {throw new ResourceAlreadyExistsException("username already valid");});

        User user = User.builder()
                .username(createDTO.getUsername())
                .fullName(createDTO.getFullName())
                .password(passwordEncoder.encode(createDTO.getPassword()))
                .avatar("")
                .description("")
                .status(Status.OFFLINE)
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional
    public User edit(String userId, EditDTO editDTO) {
        var user = findUserById(userId);

        if(StringUtils.hasText(editDTO.getFullName())) user.setFullName(editDTO.getFullName());
        if(StringUtils.hasText(editDTO.getAvatar())) user.setAvatar(editDTO.getAvatar());
        if(StringUtils.hasText(editDTO.getDescription())) user.setDescription(editDTO.getDescription());

        return userRepository.save(user);
    }

    @Override
    public void delete(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserAndTokenDTO login(LoginDTO loginDTO) {
        var savedUser = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(()-> new ResourceNotFoundException("username not found in our data"));

        changeStatus(savedUser, Status.ONLINE);

        return UserAndTokenDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .avatar(savedUser.getAvatar())
                .status(Status.ONLINE)
                .token(jwtGenerator.generateToken(savedUser))
                .build();
    }

    @Override
    public User logout(String userId) {
        var savedUser = findUserById(userId);

        changeStatus(savedUser, Status.OFFLINE);

        return savedUser;
    }

    @Override
    @Transactional
    public void editPassword(String userId, PasswordDTO passwordDTO) {
        var savedUser = findUserById(userId);

        if(passwordEncoder.matches(passwordDTO.getCurrentPassword(), savedUser.getPassword())){
            throw new BadRequestException("the current password does not match!");
        }

        savedUser.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));

        userRepository.save(savedUser);
    }

    @Override
    public GetOwnDetailsDTO getDetails(String userId) {
        var savedUser = findUserById(userId);

        return GetOwnDetailsDTO.builder()
                .fullName(savedUser.getFullName())
                .description(savedUser.getDescription())
                .status(savedUser.getStatus())
                .build();
    }

    @Override
    public GetDetailsDTO getDetails(String userId, String otherUserId) {
        var savedUser = findUserById(otherUserId);

        return GetDetailsDTO.builder()
                .username(savedUser.getUsername())
                .description(savedUser.getDescription())
                .status(savedUser.getStatus())
                .relationStatus(friendService.getRelationStatus(userId, otherUserId))
                .senderId(friendService.getSenderId(userId, otherUserId))
                .build();
    }

    @Override
    public GetMiniDetailsDTO findUser(String userId, String username) {
        var savedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user not found!"));
        return GetMiniDetailsDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .avatar(savedUser.getAvatar())
                .relationStatus(friendService.getRelationStatus(userId, savedUser.getId()))
                .senderId(friendService.getSenderId(userId, savedUser.getId()))
                .build();
    }

    private User findUserById(String userId){
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user is not found"));
    }

    private void changeStatus(User user, Status status){
        user.setStatus(status);
        userRepository.save(user);
    }
}
