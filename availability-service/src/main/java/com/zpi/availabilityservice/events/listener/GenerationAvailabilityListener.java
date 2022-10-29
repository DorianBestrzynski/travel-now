package com.zpi.availabilityservice.events.listener;

import com.zpi.availabilityservice.events.GenerationAvailabilityEvent;
import com.zpi.availabilityservice.sharedGroupAvailability.SharedGroupAvailabilityRepository;
import com.zpi.availabilityservice.sharedGroupAvailability.SharedGroupAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GenerationAvailabilityListener implements ApplicationListener<GenerationAvailabilityEvent> {

    private final SharedGroupAvailabilityService sharedGroupAvailabilityService;
    @Override
    public void onApplicationEvent(GenerationAvailabilityEvent event) {
        sharedGroupAvailabilityService.generateSharedGroupAvailability(event.getGroupId());
    }
}
