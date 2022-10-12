package com.zpi.accommodationservice.accommodationservice.accomodation_strategy;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.zpi.accommodationservice.accommodationservice.comons.Utils.LATITUDE_INDEX;
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.LONGITUDE_INDEX;

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

            result[LATITUDE_INDEX] = latitude;
            result[LONGITUDE_INDEX] = longitude;
        } catch (ApiException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
