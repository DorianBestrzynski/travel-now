package com.zpi.tripgroupservice.dto;

import com.zpi.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.commons.GroupStage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
public record TripExtendedDataDto(Long groupId, String name, Currency currency, String description, Integer votesLimit,
                                  String startLocation, String startCity, LocalDate startDate, LocalDate endDate,
                                  Double latitude, Double longitude, GroupStage groupStage, Integer minimalNumberOfDays,
                                  Integer minimalNumberOfParticipants, Long selectedAccommodationId, Long selectedSharedAvailability, Integer participantsNum) {
}
