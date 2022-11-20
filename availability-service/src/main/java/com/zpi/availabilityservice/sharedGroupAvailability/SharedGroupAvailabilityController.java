package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/shared-availability")
public class SharedGroupAvailabilityController {

    private final SharedGroupAvailabilityService sharedGroupAvailabilityService;

    @GetMapping()
    public ResponseEntity<List<SharedGroupAvailability>> getGroupSharedAvailabilities(@RequestParam Long groupId) {
        var result = sharedGroupAvailabilityService.getGroupSharedAvailabilities(groupId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/accept")
    @ResponseStatus(HttpStatus.OK)
    public void acceptSharedGroupAvailability(@RequestParam Long sharedGroupAvailabilityId) {
        sharedGroupAvailabilityService.acceptSharedGroupAvailability(sharedGroupAvailabilityId);
    }

}
