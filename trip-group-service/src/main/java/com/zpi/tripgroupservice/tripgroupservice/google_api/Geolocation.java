package com.zpi.tripgroupservice.tripgroupservice.google_api;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.zpi.tripgroupservice.tripgroupservice.commons.Utils.LATITUDE_INDEX;
import static com.zpi.tripgroupservice.tripgroupservice.commons.Utils.LONGITUDE_INDEX;

@RequiredArgsConstructor
@Component
public class Geolocation {

    @Qualifier("context")
    private final GeoApiContext context;


    public GeoApiContext context() {
        return context;
    }

    public Double[] findCoordinates(String startLocation) {
        var result = new Double[2];
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context(), startLocation).await();
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
