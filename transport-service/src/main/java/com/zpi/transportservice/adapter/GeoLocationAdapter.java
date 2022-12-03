package com.zpi.transportservice.adapter;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GeoLocationAdapter {

    private final GeoApiContext context;

    public DirectionsResult getRoute(String startingLocation, String streetAddress){
        try {
            return DirectionsApi.getDirections(context, startingLocation, streetAddress).await();
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public Long getDistance(DirectionsResult route){
        return route.routes[0].legs[0].distance.inMeters;
    }

    public Duration getDuration(DirectionsResult route){
        return Duration.ofSeconds(route.routes[0].legs[0].duration.inSeconds);
    }

}
