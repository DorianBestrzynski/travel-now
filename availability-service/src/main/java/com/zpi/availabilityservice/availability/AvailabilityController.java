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

    @GetMapping("/user")
    public ResponseEntity<List<Availability>> getUserAvailabilitiesInTripGroup(
            @RequestParam(name = "userId", required = true) Long userId,
            @RequestParam(name = "groupId", required = true) Long groupId) {

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

    @DeleteMapping("/user/{availabilityId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void addAvailability(@PathVariable Long availabilityId) {
         availabilityService.deleteAvailability(availabilityId);
    }
    @PatchMapping("/user/{availabilityId}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<Availability> changeAvailability(@PathVariable Long availabilityId, @RequestParam(name = "from", required = false) LocalDate newDateFrom,
                                   @RequestParam(name = "to", required = false) LocalDate newDateTo) {
        var result = availabilityService.changeAvailability(availabilityId, newDateFrom, newDateTo);
        return new ResponseEntity<>(result, HttpStatus.ACCEPTED );
    }
}
