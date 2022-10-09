package com.zpi.accommodationservice.accommodationservice.dto;

import java.time.LocalDate;

public record AccommodationInfoDto(Long accommodationId, String sourceLocation, String name, String streetAddress, String country, LocalDate startDate, LocalDate endDate) {
}
