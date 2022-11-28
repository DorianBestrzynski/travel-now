package com.zpi.tripgroupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zpi.tripgroupservice.commons.Currency;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record TripGroupDto(
        @NotEmpty
        @Length(max = 20)
        @JsonProperty("name")
        String name,
        @NotNull
        @JsonProperty("currency")
        Currency currency,
        @Length(max = 120)
        @JsonProperty("description")
        String description,
        @JsonProperty("votesLimit")
        Integer votesLimit,
        @NotEmpty
        @Length(max = 100)
        @JsonProperty("startLocation")
        String startLocation,
        @NotEmpty
        @Length(max = 100)
        @JsonProperty("startCity")
        String startCity,

        @JsonProperty("minimalNumberOfDays")
        Integer minimalNumberOfDays,

        @JsonProperty("minimalNumberOfParticipants")
        Integer minimalNumberOfParticipants
)
{
}
