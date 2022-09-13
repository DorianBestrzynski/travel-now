package com.zpi.tripgroupservice.tripgroupservice.user_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.exception.ApiExceptionHandler;
import com.zpi.tripgroupservice.tripgroupservice.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.USER_GROUP_ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;

    public void createUserGroup(Long creatorId, Long groupId, Integer votesInGroup) {
        var userGroup = new UserGroup(new UserGroupKey(creatorId,groupId), Role.COORDINATOR, votesInGroup);
        userGroupRepository.save(userGroup);
    }

    public boolean isUserCoordinator(Long userId, Long groupId) {
        UserGroup userGroup = userGroupRepository.findById(new UserGroupKey(userId, groupId)).orElseThrow(() -> new ApiRequestException(USER_GROUP_ENTITY_NOT_FOUND));
        return userGroup.getRole().equals(Role.COORDINATOR);
    }

    public void deletionGroupCleanUp(Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findAllById_GroupId(groupId);
        if(userGroups.isEmpty()) throw new ApiRequestException(USER_GROUP_ENTITY_NOT_FOUND);
        userGroupRepository.deleteAll(userGroups);
    }
}
