package com.zpi.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.dto.*;
import com.zpi.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.user_group.UserGroupRepository;
import com.zpi.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
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
    public ResponseEntity<List<TripExtendedDataDto>> getAllGroupsForUser(@PathVariable Long userId) {
        var result = tripGroupService.getAllGroupsForUser(userId);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/group")
    public ResponseEntity<TripGroup> createGroup(@Valid @RequestBody TripGroupDto tripGroupDto) {
        var result = tripGroupService.createGroup(tripGroupDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }
    @DeleteMapping("/group")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteGroup(@RequestParam(name = "groupId") Long groupId) {
        tripGroupService.deleteGroup(groupId);
    }

    @PatchMapping("/group")
    public ResponseEntity<TripGroup> changeGroup(@RequestParam Long groupId, @RequestBody TripGroupDto tripGroupDto) {
        var result = tripGroupService.updateGroup(groupId, tripGroupDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/data")
    public ResponseEntity<TripExtendedDataDto> getTripData(@RequestParam Long groupId){
        var tripData = tripGroupService.getTripData(groupId);
        return ResponseEntity.ok(tripData);
    }

    @GetMapping("/transport-data")
    public ResponseEntity<TripDataDto> getTripDataForTransport(@RequestParam Long groupId){
        var tripData = tripGroupService.getTripDataForTransport(groupId);
        return ResponseEntity.ok(tripData);
    }

    @GetMapping("/availability-info")
    public ResponseEntity<AvailabilityConstraintsDto> getAvailabilityConstraints(@RequestParam Long groupId){
        var availabilityConstraints = tripGroupService.getAvailabilityConstraints(groupId);
        return ResponseEntity.ok(availabilityConstraints);
    }

    @GetMapping("/accommodation")
    public ResponseEntity<AccommodationInfoDto> getAccommodation(@RequestParam Long groupId){
        var accommodation = tripGroupService.getAccommodation(groupId);
        return ResponseEntity.ok(accommodation);
    }

    @PutMapping("/accommodation")
    @ResponseStatus(HttpStatus.OK)
    public void setSelectedAccommodation(@RequestParam Long groupId, @RequestParam Long accommodationId){
        tripGroupService.setSelectedAccommodation(groupId, accommodationId);
    }

    @PutMapping("/availability")
    @ResponseStatus(HttpStatus.OK)
    public void setSelectedAvailability(@RequestParam Long groupId, @RequestParam Long availabilityId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        tripGroupService.setSelectedAvailability(groupId, availabilityId, startDate, endDate);
    }

    @PatchMapping("/currency")
    public ResponseEntity<TripGroup> setCurrency(@RequestParam Long groupId, @RequestParam Currency currency){
        var result = tripGroupService.setCurrencyInGroup(groupId, currency);
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void leaveGroup(@RequestParam Long groupId) {
        tripGroupService.leaveGroup(groupId);
    }

    @DeleteMapping("/coordinator-user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteUserFromGroup(@RequestParam Long groupId, @RequestParam Long userId) {
        tripGroupService.deleteUserFromGroup(groupId, userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void changeGroupStage(@RequestParam Long groupId) {
        tripGroupService.changeGroupStage(groupId);
    }

    @PutMapping("/selected-availability")
    @ResponseStatus(HttpStatus.OK)
    public void unselectAvailability(@RequestParam Long groupId) {
        tripGroupService.unselectAvailability(groupId);
    }

    @PutMapping("/selected-accommodation")
    @ResponseStatus(HttpStatus.OK)
    public void unselectAccommodation(@RequestParam Long groupId) {
        tripGroupService.unselectAccommodation(groupId);
    }


    @GetMapping("/sampleData")
    public String creatingSampleData() {
        var tripGroup = new TripGroup("Test1", Currency.PLN, "Opis", 2, "Barcelona", "Barcelona",3,3);
        var tripGroup1 = new TripGroup("Test2", Currency.PLN, "Opis2", 3, "Madryt", "Madryt",3,5);
        var tripGroup2 = new TripGroup("Test3", Currency.USD, "Opis3", 4, "Wroclaw", "Wroclaw",2,5);
        var tripGroup3 = new TripGroup("Test4", Currency.PLN, "Opis4", 5, "Huelva", "Huelva",3,4);
        var tripGroup4 = new TripGroup("Test5", Currency.PLN, "Opis5", 6, "Pisa", "Pisa",4,6);
        var tripGroup5 = new TripGroup("Finance Optimizer", Currency.PLN, "Grupa testujaca optymalizacje", 6, "Pisa", "Pisa", 2 ,5);
        tripGroupRepository.saveAll(List.of(tripGroup1, tripGroup2, tripGroup3, tripGroup4, tripGroup, tripGroup5));

        var userData1 = new UserGroup(new UserGroupKey(1L, tripGroup.getGroupId()), Role.COORDINATOR, 1);
        var userData2 = new UserGroup(new UserGroupKey(1L, tripGroup1.getGroupId()), Role.COORDINATOR, 1);
        var userData3 = new UserGroup(new UserGroupKey(1L, tripGroup2.getGroupId()), Role.COORDINATOR, 1);
        var userData4 = new UserGroup(new UserGroupKey(1L, tripGroup3.getGroupId()), Role.COORDINATOR, 1);
        var userData5 = new UserGroup(new UserGroupKey(2L, tripGroup4.getGroupId()), Role.COORDINATOR, 1);
        var userData6 = new UserGroup(new UserGroupKey(2L, tripGroup1.getGroupId()), Role.PARTICIPANT, 1);
        var userData7 = new UserGroup(new UserGroupKey(1L, tripGroup5.getGroupId()), Role.COORDINATOR, 1);
        var userData8 = new UserGroup(new UserGroupKey(2L, tripGroup5.getGroupId()), Role.PARTICIPANT, 1);
        var userData9 = new UserGroup(new UserGroupKey(3L, tripGroup5.getGroupId()), Role.PARTICIPANT, 1);
        var userData10 = new UserGroup(new UserGroupKey(4L, tripGroup5.getGroupId()), Role.PARTICIPANT, 1);


        userGroupRepository.saveAll(List.of(userData1, userData2, userData3, userData4, userData5, userData6, userData7, userData8, userData9, userData10));

        return "Created sample data";
    }
}
