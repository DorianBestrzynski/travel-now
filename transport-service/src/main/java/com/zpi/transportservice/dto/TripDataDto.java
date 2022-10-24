package com.zpi.transportservice.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record TripDataDto(
        @NotNull
        String startingLocation,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotNull
        Double latitude,
        @NotNull
        Double longitude) {
}
