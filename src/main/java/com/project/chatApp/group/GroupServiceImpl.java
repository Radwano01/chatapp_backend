package com.project.chatApp.group;

import com.project.chatApp.exception.BadRequestException;
import com.project.chatApp.exception.ResourceAlreadyExistsException;
import com.project.chatApp.exception.ResourceDoesNotHaveAccessException;
import com.project.chatApp.exception.ResourceNotFoundException;
import com.project.chatApp.group.dto.*;
import com.project.chatApp.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    public void save(CreateDTO createDTO, String userId) {
        var savedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var group = Group.builder()
                .chatId("group_" + UUID.randomUUID())
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .avatar(createDTO.getAvatar())
                .createdAt(new Date())
                .build();

        var groupMember = GroupMember.builder()
                .group(group)
                .user(savedUser)
                .role(Roles.OWNER)
                .joinedAt(new Date())
                .build();

        group.setMembers(List.of(groupMember));
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public Group edit(int groupId, String ownerId, EditDTO editDTO) {
        var savedGroup = findGroupById(groupId);

        boolean isOwner = isOwner(savedGroup.getMembers(), ownerId);

        if(!isOwner) throw new ResourceDoesNotHaveAccessException("access denied");

        if(editDTO.getName() != null) savedGroup.setName(editDTO.getName());
        if(editDTO.getDescription() != null) savedGroup.setDescription(editDTO.getDescription());
        if(editDTO.getAvatar() != null) savedGroup.setAvatar(editDTO.getAvatar());

        return groupRepository.save(savedGroup);
    }

    @Override
    public void delete(int groupId, String userId) {
        var savedGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));

        boolean isOwner = isOwner(savedGroup.getMembers(), userId);

        if(!isOwner){
            throw new BadRequestException("this user does not have access");
        }

        groupRepository.deleteById(groupId);
    }

    @Override
    public void changeUserRole(int groupId, String modId, String userId, Roles role) {
        var savedGroup = findGroupById(groupId);

        GroupMember memberToUpdate = savedGroup.getMembers().stream()
                        .filter(m -> m.getUser().getId().equals(userId))
                        .findFirst()
                        .orElseThrow(()-> new ResourceNotFoundException("user not found"));


        Roles modRole = getMemberRole(savedGroup.getMembers(), modId);
        Roles userRole = getMemberRole(savedGroup.getMembers(), userId);

        if(modRole.getLevel() < userRole.getLevel()){
            throw new ResourceAlreadyExistsException("This user already has higher role");
        }

        memberToUpdate.setRole(role);
        groupRepository.save(savedGroup);
    }

    @Override
    public void addUser(int groupId, String username) {
        var savedGroup = findGroupById(groupId);

        var savedUser = userRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("user not found"));


        if(findMember(savedGroup.getMembers(), savedUser.getId()) != null){
            throw new ResourceAlreadyExistsException("user already exist in the group");
        }

        var groupMember = GroupMember.builder()
                .group(savedGroup)
                .user(savedUser)
                .role(Roles.USER)
                .joinedAt(new Date())
                .build();

        savedGroup.getMembers().add(groupMember);
        groupRepository.save(savedGroup);
    }

    @Override
    @Transactional
    public void removeUser(int groupId, String modId, String userId) {
        var savedGroup = findGroupById(groupId);

        var mod = getMemberRole(savedGroup.getMembers(), modId);
        var target = getMemberRole(savedGroup.getMembers(), userId);

        if(findMember(savedGroup.getMembers(), modId) == null || findMember(savedGroup.getMembers(), userId) == null){
            throw new ResourceNotFoundException("user or mod is not found in this group");
        }

        if (mod.getLevel() < target.getLevel()) {
            throw new ResourceDoesNotHaveAccessException("Not allowed to remove higher role");
        }

        savedGroup.getMembers().removeIf(member -> member.getUser().getId().equals(userId));
        groupRepository.save(savedGroup);
    }

    @Override
    public void leaveGroup(int groupId, String userId) {
        var savedGroup = findGroupById(groupId);

        var isOwner = isOwner(savedGroup.getMembers(), userId);

        if (isOwner) {
            var newOwner = getNewOwner(savedGroup.getMembers(), userId);

            if(newOwner != null){
                newOwner.setRole(Roles.OWNER);
                savedGroup.getMembers().removeIf(m -> m.getUser().getId().equals(userId));
            }else{
                groupRepository.deleteById(groupId);
                return;
            }
        }else{
            savedGroup.getMembers().removeIf(m -> m.getUser().getId().equals(userId));
        }
        groupRepository.save(savedGroup);
    }

    @Override
    public List<GetDTO> userGroups(String userId) {
        var savedGroups = groupMemberRepository.findAllGroupsByUserId(userId);

        return savedGroups.stream().map(g ->
            GetDTO.builder()
                    .id(g.getId())
                    .name(g.getName())
                    .role(getMemberRole(g.getMembers(), userId))
                    .avatar(g.getAvatar())
                    .build()
        ).toList();
    }

    @Override
    public DetailsDTO groupDetails(int groupId) {
        var savedGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("group not found"));

        // Get all member details
        List<GetUserDetailsDTO> usersDetails = savedGroup.getMembers().stream().map(member -> GetUserDetailsDTO.builder()
            .id(member.getUser().getId())
            .username(member.getUser().getUsername())
            .fullName(member.getUser().getFullName())
            .description(member.getUser().getDescription())
            .avatar(member.getUser().getAvatar())
            .status(member.getUser().getStatus())
            .role(member.getRole())
            .build()).collect(Collectors.toList());

        return DetailsDTO.builder()
                .description(savedGroup.getDescription())
                .members(usersDetails)
                .createdAt(savedGroup.getCreatedAt())
                .build();
    }

    private Group findGroupById(int groupId){
        return groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    private boolean isOwner(List<GroupMember> members, String memberId) {
        for (GroupMember member : members) {
            if (member.getUser().getId().equals(memberId)) {
                return Roles.OWNER.getLevel() == member.getRole().getLevel();
            }
        }
        return false;
    }

    private Roles getMemberRole(List<GroupMember> groupMembers, String memberId){
        for(GroupMember member : groupMembers){
            if(member.getUser().getId().equals(memberId)){
                return member.getRole();
            }
        }
        return Roles.USER;
    }

    private GroupMember getNewOwner(List<GroupMember> members, String leavingUserId){
        for(GroupMember member : members){
            if (!member.getUser().getId().equals(leavingUserId) && member.getRole().getLevel() == Roles.ADMIN.getLevel()) {
                return member;
            }
        }
        for(GroupMember member : members){
            if (!member.getUser().getId().equals(leavingUserId) && member.getRole().getLevel() == Roles.USER.getLevel()) {
                return member;
            }
        }
        return null;
    }

    private GroupMember findMember(List<GroupMember> groupMembers, String memberId){
        for(GroupMember member : groupMembers){
            if(member.getUser().getId().equals(memberId)){
                return member;
            }
        }
        return null;
    }
}
