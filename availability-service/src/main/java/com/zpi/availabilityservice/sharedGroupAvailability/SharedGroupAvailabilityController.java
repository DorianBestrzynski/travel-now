package com.zpi.availabilityservice.sharedGroupAvailability;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/shared-availability")
public class SharedGroupAvailabilityController {

    private final SharedGroupAvailabilityService sharedGroupAvailabilityService;

}
