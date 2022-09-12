package com.zpi.availabilityservice.dto;

import java.time.LocalDate;

public record AvailabilityDto(Long userId, Long groupId, LocalDate dateFrom, LocalDate dateTo) {
}
