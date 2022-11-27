package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/shared-availability")
public class SharedGroupAvailabilityController {

    private final SharedGroupAvailabilityService sharedGroupAvailabilityService;

    @GetMapping("/list")
    public ResponseEntity<List<SharedGroupAvailability>> getGroupSharedAvailabilities(@RequestParam Long groupId) {
        var result = sharedGroupAvailabilityService.getGroupSharedAvailabilities(groupId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/accept")
    @ResponseStatus(HttpStatus.OK)
    public void acceptSharedGroupAvailability(@RequestParam Long sharedGroupAvailabilityId) {
        sharedGroupAvailabilityService.acceptSharedGroupAvailability(sharedGroupAvailabilityId);
    }

    @PostMapping()
    public ResponseEntity<SharedGroupAvailability> addSharedGroupAvailability(@RequestParam LocalDate dateFrom, @RequestParam LocalDate dateTo, @RequestParam Long groupId) {
        var result = sharedGroupAvailabilityService.createSharedGroupAvailability(dateFrom, dateTo, groupId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<SharedGroupAvailability> getGroupSharedAvailability(@RequestParam Long sharedGroupAvailabilityId) {
        var result = sharedGroupAvailabilityService.getSharedGroupAvailability(sharedGroupAvailabilityId);
        return ResponseEntity.ok(result);
    }



}
