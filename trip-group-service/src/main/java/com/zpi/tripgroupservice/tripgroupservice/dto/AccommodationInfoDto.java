package com.zpi.tripgroupservice.tripgroupservice.dto;

public record AccommodationInfoDto(String streetAddress, String city, Double destinationLatitude, Double destinationLongitude, Long groupId) {
    public AccommodationInfoDto() {
        this(null, null, null, null, null);
    }
}
