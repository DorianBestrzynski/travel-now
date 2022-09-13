package com.zpi.tripgroupservice.tripgroupservice.trip_group;
import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.GroupStage;
import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("api/v1/trip-group")
@RestController
@RequiredArgsConstructor
public class TripGroupController {

    private final TripGroupService tripGroupService;

    private final TripGroupRepository tripGroupRepository;

    private final UserGroupRepository userGroupRepository;


    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<TripGroup>> getAllGroupsForUser(@PathVariable Long userId){
        var result = tripGroupService.getAllGroupsForUser(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sampleData")
    public String creatingSampleData(){
        var tripGroup = new TripGroup("Test1", Currency.PLN, "Opis", 2, "Barcelona", GroupStage.PLANNING_STAGE, null);
        var tripGroup1 = new TripGroup("Test2", Currency.PLN, "Opis2", 3, "Madryt", GroupStage.TRIP_STAGE, null);
        var tripGroup2 = new TripGroup("Test3", Currency.USD, "Opis3", 4, "Wroclaw", GroupStage.AFTER_TRIP_STAGE, null);
        var tripGroup3 = new TripGroup("Test4", Currency.PLN, "Opis4", 5, "Huelva", GroupStage.PLANNING_STAGE, null);
        var tripGroup4 = new TripGroup("Test5", Currency.PLN, "Opis5", 6, "Pisa", GroupStage.TRIP_STAGE, null);
        tripGroupRepository.saveAll(List.of(tripGroup1,tripGroup2,tripGroup3,tripGroup4, tripGroup));

        var userData1 = new UserGroup(new UserGroupKey(1L,tripGroup.getGroupId()), Role.COORDINATOR, 1);
        var userData2 = new UserGroup(new UserGroupKey(1L,tripGroup1.getGroupId()), Role.COORDINATOR, 1);
        var userData3 = new UserGroup(new UserGroupKey(1L,tripGroup2.getGroupId()), Role.COORDINATOR, 1);
        var userData4 = new UserGroup(new UserGroupKey(1L,tripGroup3.getGroupId()), Role.COORDINATOR, 1);
        var userData5 = new UserGroup(new UserGroupKey(2L,tripGroup4.getGroupId()), Role.COORDINATOR, 1);
        var userData6 = new UserGroup(new UserGroupKey(2L,tripGroup1.getGroupId()), Role.PARTICIPANT, 1);

        userGroupRepository.saveAll(List.of(userData1,userData2,userData3,userData4,userData5, userData6));

        return "Created sample data";
        }
        

}
