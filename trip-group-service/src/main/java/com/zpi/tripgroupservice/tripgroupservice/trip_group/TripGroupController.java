package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/trip-group")
@RestController
@RequiredArgsConstructor
public class TripGroupController {

    private final UserGroupRepository userGroupRepository;

    @GetMapping("/usr")
    public String createUserGroup(){
        UserGroupKey userGroupKey = new UserGroupKey(2L ,1L);
        UserGroup userGroup = new UserGroup(userGroupKey, Role.PARTICIPANT, 2);
        userGroupRepository.save(userGroup);
        return "Hello Boba";
    }
}
