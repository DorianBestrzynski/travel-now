package com.zpi.tripgroupservice.tripgroupservice.user_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.USER_NOT_A_MEMBER;

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
                           .orElseThrow(() -> new IllegalArgumentException(USER_NOT_A_MEMBER));
    }
}
