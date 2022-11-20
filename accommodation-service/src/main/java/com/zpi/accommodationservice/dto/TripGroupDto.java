package com.zpi.accommodationservice.dto;

import com.zpi.accommodationservice.comons.Currency;
import com.zpi.accommodationservice.comons.GroupStage;

import java.time.LocalDate;

public record TripGroupDto(Long groupId, String name, Currency currency, String description, Integer votesLimit, String startLocation,
                           String startCity, LocalDate startDate, LocalDate endDate, Double latitude, Double longitude,
                           GroupStage groupStage, Integer minimalNumberOfDays, Integer minimalNumberOfParticipants,
                           Long selectedAccommodationId, Long selectedSharedAvailability) {

}
