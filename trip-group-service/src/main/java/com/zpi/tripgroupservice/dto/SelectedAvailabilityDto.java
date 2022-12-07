package com.zpi.tripgroupservice.dto;

import java.time.LocalDate;

public record SelectedAvailabilityDto(Long groupId, Long availabilityId, LocalDate startDate, LocalDate endDate) {
}
