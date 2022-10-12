package com.zpi.dayplanservice.attraction;

import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.zpi.dayplanservice.dto.AttractionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionService {
    private final AttractionRepository attractionRepository;

    private final GeoApiContext context;

    public List<Attraction> getAllAttractionsForDay(Long groupId, Long dayPlanId) {
        return null;
    }

    public Attraction addAttraction(AttractionDto attractionDto) {
        try {
            var test = PlacesApi.findPlaceFromText(context, attractionDto.name(), FindPlaceFromTextRequest.InputType.TEXT_QUERY)
                                .fields(
                                        FindPlaceFromTextRequest.FieldMask.NAME,
                                        FindPlaceFromTextRequest.FieldMask.GEOMETRY,
                                        FindPlaceFromTextRequest.FieldMask.PHOTOS,
                                        FindPlaceFromTextRequest.FieldMask.OPENING_HOURS).await();
            System.out.println(test);
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Attraction deleteAccommodation(Long attractionId, Long dayPlanId) {
        return null;
    }

    public Attraction editAccommodation(Long accommodationId, Long userId, AttractionDto accommodationDto) {
        return null;
    }
}
