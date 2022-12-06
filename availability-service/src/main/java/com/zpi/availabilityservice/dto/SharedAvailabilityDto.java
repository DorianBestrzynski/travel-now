package com.zpi.availabilityservice.dto;

import java.time.LocalDate;

public record SharedAvailabilityDto(LocalDate dateFrom, LocalDate dateTo) {
}