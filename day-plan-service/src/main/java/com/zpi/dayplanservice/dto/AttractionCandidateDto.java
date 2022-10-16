package com.zpi.dayplanservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class AttractionCandidateDto {
        @Setter
        @Getter
        @NotNull
        @JsonProperty("attractionName")
        private String attractionName;

        @Setter
        @Getter
        @JsonProperty("address")
        private String address;

        @Setter
        @Getter
        @JsonProperty("openingHours")
        private String[] openingHours;

        @Setter
        @Getter
        @NotNull
        @JsonProperty("latitude")
        private Double latitude;

        @Setter
        @Getter
        @NotNull
        @JsonProperty("longitude")
        private Double longitude;

        @Setter
        @Getter
        @NotNull
        @JsonProperty("placeId")
        private String placeId;

        @Setter
        @Getter
        @NotNull
        @JsonProperty("photoLink")
        private String photoLink;

        @Setter
        @Getter
        @JsonProperty("url")
        private String url;


        public AttractionCandidateDto(String attractionName, Double latitude, Double longitude, String placeId, String photoLink) {
            this.attractionName = attractionName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeId = placeId;
            this.photoLink = photoLink;
        }

}
