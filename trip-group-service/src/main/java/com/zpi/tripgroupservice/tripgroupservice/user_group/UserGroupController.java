package com.zpi.tripgroupservice.tripgroupservice.user_group;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user-group")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    @GetMapping("/role")
    public ResponseEntity<Boolean> isUserCoordinator(@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId){
        var result = userGroupService.isUserCoordinator(userId, groupId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/group")
    public ResponseEntity<Boolean> checkIfUserIsPartOfTheGroup(@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId) {
        var result = userGroupService.checkIfUserIsInGroup(userId, groupId);
        return ResponseEntity.ok(result);
    }

}
