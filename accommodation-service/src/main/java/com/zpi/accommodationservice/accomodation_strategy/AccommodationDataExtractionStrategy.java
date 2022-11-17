package com.zpi.accommodationservice.accomodation_strategy;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.zpi.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.comons.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public interface AccommodationDataExtractionStrategy {

    AccommodationDataDto extractDataFromUrl(String url);

    String getServiceName();

    GeoApiContext context();

    default Double[] getStreetCoordinates(String street) {
        var result = new Double[2];
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context(), street).await();
            var latitude = BigDecimal.valueOf(results[0].geometry.location.lat).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            var longitude = BigDecimal.valueOf(results[0].geometry.location.lng).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

            result[Utils.LATITUDE_INDEX] = latitude;
            result[Utils.LONGITUDE_INDEX] = longitude;
        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
