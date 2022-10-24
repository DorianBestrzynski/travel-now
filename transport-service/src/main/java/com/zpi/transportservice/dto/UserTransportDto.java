package com.zpi.transportservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserTransportDto(
        @NotNull
        @JsonProperty("duration")
        Duration duration,
        @JsonProperty("price")
        BigDecimal price,
        @NotEmpty
        @JsonProperty("source")
        String source,
        @NotEmpty
        @JsonProperty("destination")
        String destination,
        @NotNull
        @JsonProperty("startDate")
        LocalDate startDate,
        @NotNull
        @JsonProperty("endDate")
        LocalDate endDate,
        @NotEmpty
        @JsonProperty("meanOfTransport")
        String meanOfTransport,
        @JsonProperty("description")
        String description,
        @JsonProperty("meetingTime")
        LocalDateTime meetingTime,
        @JsonProperty("link")
        String link
){
}
