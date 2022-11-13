package com.zpi.transportservice.dto;



public record AccommodationInfoDto(String streetAddress, String city, Double destinationLatitude, Double destinationLongitude, Long groupId, Long creatorId) {
    public AccommodationInfoDto(){
        this(null,null,null,null,1L,null);
    }
}
