package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

}
