package com.zpi.tripgroupservice.tripgroupservice.user_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;

    public void createUserGroup(Long creatorId, Long groupId, Integer votesInGroup) {
        var userGroup = new UserGroup(new UserGroupKey(creatorId,groupId), Role.COORDINATOR, votesInGroup);
        userGroupRepository.save(userGroup);
    }
}
