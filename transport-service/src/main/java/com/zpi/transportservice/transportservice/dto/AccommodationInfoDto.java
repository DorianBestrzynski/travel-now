package com.zpi.transportservice.transportservice.dto;

import java.time.LocalDate;

public record AccommodationInfoDto(Long accommodationId, String startingLocation, String name, String streetAddress, String country, LocalDate startDate, LocalDate endDate) {
}
