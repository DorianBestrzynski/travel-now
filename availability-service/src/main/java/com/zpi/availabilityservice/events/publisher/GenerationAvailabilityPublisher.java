package com.zpi.availabilityservice.events.publisher;

import com.zpi.availabilityservice.events.GenerationAvailabilityEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerationAvailabilityPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishAvailabilityGenerationEvent(final Long groupId) {
        System.out.println("Publishing generation availability event.");
        GenerationAvailabilityEvent customSpringEvent = new GenerationAvailabilityEvent(this, groupId);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }


}
