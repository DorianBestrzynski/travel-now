package com.zpi.transportservice.transportservice.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record TripDataDto(
        @NotNull
        String startingLocation,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate) {
}
