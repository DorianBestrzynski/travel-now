package com.zpi.accommodationservice.accommodationservice.dto;

import com.zpi.accommodationservice.accommodationservice.comons.TransportType;

import java.math.BigDecimal;
import java.time.Duration;

public record TransportDto(Long transportId, TransportType transportType, Duration duration, BigDecimal price, String source, String destination, String link) {
}
