package com.zpi.tripgroupservice.tripgroupservice.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record TripGroupDto(
        @NotEmpty
        @JsonProperty("name")
        String name,
        @NotNull
        @JsonProperty("currency")
        Currency currency,
        @JsonProperty("description")
        String description,
        @JsonProperty("votesLimit")
        Integer votesLimit,
        @NotEmpty
        @JsonProperty("startLocation")
        String startLocation) {
}
