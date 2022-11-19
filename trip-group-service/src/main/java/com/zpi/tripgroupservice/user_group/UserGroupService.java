package com.zpi.tripgroupservice.user_group;

import com.zpi.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.exception.ExceptionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGroupService {
    private final UserGroupRepository userGroupRepository;

    public UserGroup createUserGroup(Long creatorId, Long groupId, Integer votesInGroup) {
        var userGroup = new UserGroup(new UserGroupKey(creatorId,groupId), Role.COORDINATOR, votesInGroup);
        return userGroupRepository.save(userGroup);
    }

    public UserGroup createUserGroup(UserGroupKey key, Role role, Integer votesInGroup) {
        var userGroup = new UserGroup(key, role, votesInGroup);
        return userGroupRepository.save(userGroup);
    }

    public boolean exists(UserGroupKey key) {
        return userGroupRepository.existsById(key);
    }

    public UserGroup getUserGroupById(UserGroupKey key) {
        return userGroupRepository.findById(key)
                           .orElseThrow(() -> new IllegalArgumentException(ExceptionInfo.USER_NOT_A_MEMBER));
    }

    public boolean isUserCoordinator(Long userId, Long groupId) {
        UserGroup userGroup = userGroupRepository.findById(new UserGroupKey(userId, groupId)).orElse(null);
        if(userGroup != null)
            return userGroup.getRole().equals(Role.COORDINATOR);
        else return false;
    }

    public void deletionGroupCleanUp(Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findAllById_GroupId(groupId);
        if(userGroups.isEmpty()) throw new ApiRequestException(ExceptionInfo.USER_GROUP_ENTITY_NOT_FOUND);
        userGroupRepository.deleteAll(userGroups);
    }

    public Boolean checkIfUserIsInGroup(Long userId, Long groupId){
        if(userId == null || groupId == null || userId < 0 || groupId < 0){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_USER_ID_GROUP_ID);
        }
        return userGroupRepository.existsById(new UserGroupKey(userId, groupId));
    }

    public void deleteUserFromGroup(Long groupId, Long userId) {
        userGroupRepository.deleteById(new UserGroupKey(userId, groupId));
    }
}
