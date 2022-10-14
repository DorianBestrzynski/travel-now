package com.zpi.accommodationservice.accommodationservice.dto;

public record AccommodationDataDto(String name, String streetAddress, String city, String country, String region, String imageLink,
                                   String url, Double latitude, Double longitude) {
}
