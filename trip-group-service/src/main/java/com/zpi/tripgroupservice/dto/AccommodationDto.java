package com.zpi.tripgroupservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
public class AccommodationDto {
    private Long accommodationId;

    private Long groupId;

    private Long creator_id;

    private String name;

    private String streetAddress;

    private String city;

    private String country;

    private String region;

    private String description;

    private String imageLink;

    private String sourceLink;

    private Integer givenVotes = 0;

    private BigDecimal price;

    private Double latitude;

    private Double longitude;

    public AccommodationDto(Long groupId, Long creator_id, String name, String streetAddress, String country, String region, String description, String imageLink, String sourceLink, BigDecimal price, Double latitude, Double longitude) {
        this.groupId = groupId;
        this.creator_id = creator_id;
        this.name = name;
        this.streetAddress = streetAddress;
        this.country = country;
        this.region = region;
        this.description = description;
        this.imageLink = imageLink;
        this.sourceLink = sourceLink;
        this.price = price;
        this.givenVotes = 3;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public AccommodationDto(Long groupId, Long creator_id, String name, String streetAddress, String city, String country, String region, String description, String imageLink, String sourceLink, BigDecimal price, Double latitude, Double longitude) {
        this.groupId = groupId;
        this.creator_id = creator_id;
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.country = country;
        this.region = region;
        this.givenVotes = 3;
        this.description = description;
        this.imageLink = imageLink;
        this.sourceLink = sourceLink;
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setGivenVotes(Integer givenVotes) {
        if(this.givenVotes > -1)
            this.givenVotes = givenVotes;
    }
}
