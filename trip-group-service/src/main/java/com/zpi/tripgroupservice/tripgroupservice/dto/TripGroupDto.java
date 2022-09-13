package com.zpi.tripgroupservice.tripgroupservice.dto;
import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record TripGroupDto(
        @NotEmpty String name,
        @NotNull Currency currency,
        String description,
        Integer votesLimit,
        @NotEmpty String startLocation) {
}
