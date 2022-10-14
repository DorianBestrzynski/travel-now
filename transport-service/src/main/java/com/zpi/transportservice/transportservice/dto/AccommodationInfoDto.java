package com.zpi.transportservice.transportservice.dto;


public record AccommodationInfoDto(String streetAddress, String city, Double destinationLatitude, Double destinationLongitude, Long groupId) {
}
