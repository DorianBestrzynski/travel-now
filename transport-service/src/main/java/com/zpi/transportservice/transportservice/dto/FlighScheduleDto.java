package com.zpi.transportservice.transportservice.dto;

import java.time.LocalDateTime;

public record FlighScheduleDto(LocalDateTime departureTime, LocalDateTime arrivalTime) {
}
