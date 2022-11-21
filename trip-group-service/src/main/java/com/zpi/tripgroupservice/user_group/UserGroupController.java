package com.zpi.tripgroupservice.user_group;

import com.zpi.tripgroupservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Boolean> isUserPartOfTheGroup(@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId) {
        var result = userGroupService.checkIfUserIsInGroup(userId, groupId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/participants")
    public ResponseEntity<List<UserDto>> getAllGroupParticipants(@RequestParam(name = "groupId") Long groupId) {
        var result = userGroupService.getAllUsersInGroup(groupId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/coordinators")
    public ResponseEntity<List<UserDto>> getAllGroupCoordinators(@RequestParam(name = "groupId") Long groupId) {
        var result = userGroupService.getAllCoordinatorsInGroup(groupId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/coordinator")
    @ResponseStatus(HttpStatus.OK)
    public void set(@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId) {
        userGroupService.setCoordinator(groupId, userId);
    }

}
