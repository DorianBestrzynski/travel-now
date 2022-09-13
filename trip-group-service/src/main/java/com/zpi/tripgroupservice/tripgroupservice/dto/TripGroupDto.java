package com.zpi.tripgroupservice.tripgroupservice.dto;

import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.GroupStage;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record TripGroupDto(
        @NotNull Long creatorId,
        @NotEmpty String name,
        @NotNull Currency currency,
        String description,
        Integer votesLimit,
        @NotEmpty String startLocation,
        @NotNull GroupStage groupStage) {
}
