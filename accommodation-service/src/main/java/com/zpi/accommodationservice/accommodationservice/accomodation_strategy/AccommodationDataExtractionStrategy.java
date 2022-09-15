package com.zpi.accommodationservice.accommodationservice.accomodation_strategy;

import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;

public interface AccommodationDataExtractionStrategy {
    AccommodationDataDto extractDataFromUrl(String url);
    String getServiceName();
}
