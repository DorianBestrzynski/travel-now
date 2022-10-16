package com.zpi.dayplanservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AttractionCandidateDto {
    @Setter
    @Getter
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
    @JsonProperty("latitude")
    private Double latitude;

    @Setter
    @Getter
    @JsonProperty("longitude")
    private Double longitude;

    @Setter
    @Getter
    @JsonProperty("placeId")
    private String placeId;

    @Setter
    @Getter
    @JsonProperty("photoLink")
    private String photoLink;

    @Setter
    @Getter
    @JsonProperty("url")
    private String url;

    @Setter
    @Getter
    @JsonProperty("description")
    private String description;

    public AttractionCandidateDto(String attractionName, Double latitude, Double longitude, String placeId,
                                  String photoLink) {
        this.attractionName = attractionName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.photoLink = photoLink;
    }

    public AttractionCandidateDto(String attractionName, Double latitude, Double longitude, String placeId,
                                  String photoLink, String address) {
        this.attractionName = attractionName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.photoLink = photoLink;
        this.address = address;
    }
}
