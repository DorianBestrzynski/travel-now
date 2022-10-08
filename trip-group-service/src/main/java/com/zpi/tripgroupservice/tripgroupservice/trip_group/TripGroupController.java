package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupRepository;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/v1/trip-group")
@RestController
@RequiredArgsConstructor
public class TripGroupController {
    private final TripGroupService tripGroupService;
    private final TripGroupRepository tripGroupRepository;
    private final UserGroupService userGroupService;
    private final UserGroupRepository userGroupRepository;

    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<TripGroup>> getAllGroupsForUser(@PathVariable Long userId) {
        var result = tripGroupService.getAllGroupsForUser(userId);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/group")
    public ResponseEntity<TripGroup> createGroup(@RequestParam Long userId,
                                                 @Valid @RequestBody TripGroupDto tripGroupDto) {
        var result = tripGroupService.createGroup(userId, tripGroupDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }
    @DeleteMapping("/group")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteGroup(@RequestParam(name = "groupId") Long groupId, @RequestParam(name = "userId") Long userId) {
        tripGroupService.deleteGroup(groupId, userId);
    }
    @PatchMapping("/group")
    public ResponseEntity<TripGroup> changeGroup(@RequestParam Long groupId, @RequestParam Long userId,
                                                 @RequestBody TripGroupDto tripGroupDto) {
        var result = tripGroupService.updateGroup(groupId, userId, tripGroupDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/location")
    public ResponseEntity<String> getStartLocation(@RequestParam Long groupId){
        var startingLocation = tripGroupService.getStartLocation(groupId);
        return ResponseEntity.ok(startingLocation);
    }

    @GetMapping("/sampleData")
    public String creatingSampleData() {
        var tripGroup = new TripGroup("Test1", Currency.PLN, "Opis", 2, "Barcelona");
        var tripGroup1 = new TripGroup("Test2", Currency.PLN, "Opis2", 3, "Madryt");
        var tripGroup2 = new TripGroup("Test3", Currency.USD, "Opis3", 4, "Wroclaw");
        var tripGroup3 = new TripGroup("Test4", Currency.PLN, "Opis4", 5, "Huelva");
        var tripGroup4 = new TripGroup("Test5", Currency.PLN, "Opis5", 6, "Pisa");
        tripGroupRepository.saveAll(List.of(tripGroup1, tripGroup2, tripGroup3, tripGroup4, tripGroup));

        var userData1 = new UserGroup(new UserGroupKey(1L, tripGroup.getGroupId()), Role.COORDINATOR, 1);
        var userData2 = new UserGroup(new UserGroupKey(1L, tripGroup1.getGroupId()), Role.COORDINATOR, 1);
        var userData3 = new UserGroup(new UserGroupKey(1L, tripGroup2.getGroupId()), Role.COORDINATOR, 1);
        var userData4 = new UserGroup(new UserGroupKey(1L, tripGroup3.getGroupId()), Role.COORDINATOR, 1);
        var userData5 = new UserGroup(new UserGroupKey(2L, tripGroup4.getGroupId()), Role.COORDINATOR, 1);
        var userData6 = new UserGroup(new UserGroupKey(2L, tripGroup1.getGroupId()), Role.PARTICIPANT, 1);

        userGroupRepository.saveAll(List.of(userData1, userData2, userData3, userData4, userData5, userData6));

        return "Created sample data";
    }
}
