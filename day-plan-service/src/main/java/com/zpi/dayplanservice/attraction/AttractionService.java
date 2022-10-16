package com.zpi.dayplanservice.attraction;

import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResult;
import com.zpi.dayplanservice.dto.AttractionCandidateDto;
import com.zpi.dayplanservice.dto.AttractionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionService {
    private final AttractionRepository attractionRepository;

    private final GeoApiContext context;

    public List<Attraction> getAllAttractionsForDay(Long groupId, Long dayPlanId) {
        return null;
    }

    public List<AttractionCandidateDto> findCandidates(AttractionDto attractionDto) {
        List<AttractionCandidateDto> result = new ArrayList<>();
        try {
            var foundCandidates = PlacesApi.findPlaceFromText(context, attractionDto.name(), FindPlaceFromTextRequest.InputType.TEXT_QUERY)
                                           .fields(
                                                   FindPlaceFromTextRequest.FieldMask.NAME,
                                                   FindPlaceFromTextRequest.FieldMask.GEOMETRY,
                                                   FindPlaceFromTextRequest.FieldMask.PHOTOS,
                                                   FindPlaceFromTextRequest.FieldMask.OPENING_HOURS,
                                                   FindPlaceFromTextRequest.FieldMask.PLACE_ID)
                                           .await();

            result = convertToAttractionCandidateDto(foundCandidates.candidates);
            getUrlAndOpeningHours(result);
            System.out.println(foundCandidates);
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Attraction deleteAccommodation(Long attractionId, Long dayPlanId) {
        return null;
    }

    public Attraction editAccommodation(Long accommodationId, Long userId, AttractionDto accommodationDto) {
        return null;
    }

    private List<AttractionCandidateDto> convertToAttractionCandidateDto(PlacesSearchResult[] foundCandidates) {
        var result = new ArrayList<AttractionCandidateDto>();
        for (PlacesSearchResult foundCandidate : foundCandidates) {
            var candidate = new AttractionCandidateDto(foundCandidate.name,
                                                       foundCandidate.geometry.location.lat,
                                                       foundCandidate.geometry.location.lng,
                                                       foundCandidate.placeId,
                                                       foundCandidate.photos[0].photoReference);
            result.add(candidate);
        }
        return result;
    }

    private List<AttractionCandidateDto> getUrlAndOpeningHours(List<AttractionCandidateDto> candidates)  {
        for (AttractionCandidateDto candidate : candidates) {
            try {
                var placeDetails = PlacesApi.placeDetails(context, candidate.getPlaceId())
                                            .fields(PlaceDetailsRequest.FieldMask.OPENING_HOURS,
                                                    PlaceDetailsRequest.FieldMask.URL,
                                                    PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS)
                                            .await();
                candidate.setUrl(placeDetails.url.toString());
                candidate.setOpeningHours(placeDetails.openingHours.weekdayText);
                candidate.setAddress(placeDetails.formattedAddress);
            } catch (ApiException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return candidates;
    }

}
