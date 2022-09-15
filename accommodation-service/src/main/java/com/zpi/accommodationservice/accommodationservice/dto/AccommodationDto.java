package com.zpi.accommodationservice.accommodationservice.dto;

import java.math.BigDecimal;

public record AccommodationDto(Long groupId, Long creatorId, String accommodationLink, String description, BigDecimal price) {
}
