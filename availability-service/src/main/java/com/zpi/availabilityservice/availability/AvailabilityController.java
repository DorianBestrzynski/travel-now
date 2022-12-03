package com.zpi.availabilityservice.availability;

import com.zpi.availabilityservice.dto.AvailabilityDto;
import com.zpi.availabilityservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/availability")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;
    private final AvailabilityRepository availabilityRepository;

    @GetMapping("/user")
    public ResponseEntity<List<Availability>> getUserAvailabilitiesInTripGroup(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "groupId") Long groupId) {

        var result = availabilityService.getUserAvailabilitiesInTripGroup(userId, groupId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Map<Long, List<Availability>>> getAvailabilitiesInTripGroup(@PathVariable Long groupId) {
        var result = availabilityService.getAvailabilitiesInTripGroup(groupId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Map<UserDto, List<Availability>>> getAvailabilitiesInTripGroupWithUserData(@PathVariable Long groupId) {
        var result = availabilityService.getAvailabilitiesInTripGroupWithUserData(groupId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user")
    public ResponseEntity<Availability> addAvailability(@RequestBody AvailabilityDto availabilityDto) {
        var result = availabilityService.addNewAvailability(availabilityDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/user")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteAvailability(@RequestParam Long availabilityId, @RequestParam Long groupId) {
         availabilityService.deleteAvailability(availabilityId, groupId);
    }

    @DeleteMapping("/all-user")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void deleteAllAvailabilitiesForUser(@RequestParam Long userId, @RequestParam Long groupId) {
        availabilityService.deleteAllAvailabilitiesForUser(userId, groupId);
    }

    @PatchMapping("/user/{availabilityId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<Availability> changeAvailability(@PathVariable Long availabilityId,
                                                           @RequestParam(name = "from", required = false) LocalDate newDateFrom,
                                                           @RequestParam(name = "to", required = false) LocalDate newDateTo) {
        var result = availabilityService.changeAvailability(availabilityId, newDateFrom, newDateTo);
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED );
    }

    @GetMapping("sampleData")
    public String createTestData(){
        Availability availability = new Availability(1L, 342L, LocalDate.of(2022, 10, 27), LocalDate.of(2022, 10, 27));
        Availability availability2 = new Availability(1L, 342L, LocalDate.of(2022, 10, 29), LocalDate.of(2022, 11, 1));
        Availability availability3 = new Availability(2L, 342L, LocalDate.of(2022, 10, 27), LocalDate.of(2022, 10, 27));
        Availability availability4 = new Availability(2L, 342L, LocalDate.of(2022, 10, 29), LocalDate.of(2022, 11, 1));
        Availability availability5 = new Availability(3L, 342L, LocalDate.of(2022, 10, 29), LocalDate.of(2022, 10, 31));
        Availability availability6 = new Availability(4L, 342L, LocalDate.of(2022, 10, 29), LocalDate.of(2022, 10, 30));
        Availability availability7 = new Availability(5L, 342L, LocalDate.of(2022, 10, 30), LocalDate.of(2022, 10, 31));

        availabilityRepository.saveAll(List.of(availability, availability2, availability3, availability4, availability5, availability6, availability7));
        return "Created test data";
    }
    @GetMapping("triggerAvailabilityGeneration")
    public String triggerAvailabilityGeneration(@RequestParam Long groupId){
        availabilityService.trigger(groupId);
        return "triggerred";
    }
}
