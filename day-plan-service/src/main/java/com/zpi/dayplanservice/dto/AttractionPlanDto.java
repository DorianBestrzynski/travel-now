package com.zpi.dayplanservice.dto;

import com.zpi.dayplanservice.attraction.Attraction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AttractionPlanDto {
    private Attraction attraction;
    private Double distanceToNextAttraction;

    public AttractionPlanDto(Attraction attraction) {
        this(attraction, null);
    }
}