package com.zpi.dayplanservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public record AttractionCandidateDto(
        @NotNull
        @JsonProperty("attractionName")
        String attractionName,

        @JsonProperty("openingHours")
        LocalTime openingHours,

        @JsonProperty("closingHours")
        LocalTime closingHours,

        @NotNull
        @JsonProperty("latitude")
        Double latitude,

        @NotNull
        @JsonProperty("longitude")
        Double longitude,

        @NotNull
        @JsonProperty("placeId")
        String placeId,

        @NotNull
        @JsonProperty("photoLink")
        String photoLink,

        @JsonProperty("url")
        String url

) {
        public AttractionCandidateDto(String attractionName, Double latitude, Double longitude, String placeId, String photoLink) {
                this(attractionName, null, null, latitude, longitude, placeId, photoLink, null);
        }
}
