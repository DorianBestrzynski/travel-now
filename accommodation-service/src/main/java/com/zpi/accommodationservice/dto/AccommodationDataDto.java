package com.zpi.accommodationservice.dto;

public record AccommodationDataDto(String name, String streetAddress, String city, String country, String region, String imageLink,
                                   String sourceLink, Double latitude, Double longitude) {
}
