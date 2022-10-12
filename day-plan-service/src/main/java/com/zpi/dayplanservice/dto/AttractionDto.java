package com.zpi.dayplanservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public record AttractionDto(
        @NotNull
        @JsonProperty("dayPlanId")
        Long dayPlanId,

        @NotNull
        @JsonProperty("name")
        String name
) {
}
