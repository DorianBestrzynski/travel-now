package com.zpi.transportservice.transportservice.dto;

import java.time.LocalDateTime;

public record FlightDto(LocalDateTime departureTime, LocalDateTime arrivalTime) {
}
