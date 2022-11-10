package com.zpi.tripgroupservice.tripgroupservice.dto;

import java.time.LocalDate;

public record TripDataDto(String startingLocation, LocalDate startDate, LocalDate endDate, Double latitude, Double longitude) { }
