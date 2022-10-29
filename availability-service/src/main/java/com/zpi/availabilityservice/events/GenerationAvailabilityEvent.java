package com.zpi.availabilityservice.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class GenerationAvailabilityEvent extends ApplicationEvent {
    private Long groupId;
    public GenerationAvailabilityEvent(Object source, Long groupId) {
        super(source);
        this.groupId = groupId;
    }
}
