package com.zpi.tripgroupservice.user_group;

import com.zpi.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.dto.UserDto;
import com.zpi.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.exception.ExceptionInfo;
import com.zpi.tripgroupservice.proxy.AppUserProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserGroupService {
    private final UserGroupRepository userGroupRepository;
    private final AppUserProxy appUserProxy;

    private static final String INNER_COMMUNICATION = "microserviceCommunication";

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

    public Integer getNumberOfParticipants(Long groupId) {
        return userGroupRepository.countAllById_GroupId(groupId);
    }

    public List<UserDto> getAllUsersInGroup(Long groupId) {
        if(groupId == null || groupId < 0){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_GROUP_ID);
        }


        return appUserProxy.getUsersDtos(INNER_COMMUNICATION,
                                         userGroupRepository.findAllById_GroupId(groupId)
                                                            .stream()
                                                            .mapToLong(ug -> ug.getId().getUserId())
                                                            .boxed()
                                                            .collect(Collectors.toList()));
    }

    public List<UserDto> getAllCoordinatorsInGroup(Long groupId) {
        if(groupId == null || groupId < 0){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_GROUP_ID);
        }


        return appUserProxy.getUsersDtos(INNER_COMMUNICATION,
                                         userGroupRepository.findAllById_GroupIdAndRoleEquals(groupId, Role.COORDINATOR)
                                                            .stream()
                                                            .mapToLong(ug -> ug.getId().getUserId())
                                                            .boxed()
                                                            .collect(Collectors.toList()));
    }
}
