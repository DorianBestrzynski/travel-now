package com.zpi.dayplanservice.attraction;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResult;
import com.zpi.dayplanservice.day_plan.DayPlanService;
import com.zpi.dayplanservice.dto.AttractionCandidateDto;
import com.zpi.dayplanservice.mapstruct.MapStructMapper;
import com.zpi.dayplanservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionService {
    private final AttractionRepository attractionRepository;

    private final DayPlanService dayPlanService;

    private final GeoApiContext context;

    private final MapStructMapper mapstructMapper;

    private final TripGroupProxy tripGroupProxy;

    public List<Attraction> getAllAttractionsForDay(Long groupId, Long dayPlanId) {
        return null;
    }

    public List<AttractionCandidateDto> findCandidates(String name) {
        List<AttractionCandidateDto> result = new ArrayList<>();
        try {
            var foundCandidates = PlacesApi.textSearchQuery(context, name).await();

            result = convertToAttractionCandidateDto(foundCandidates.results);
            getUrl(result);
            System.out.println(foundCandidates);
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Transactional
    public Attraction deleteAttraction(Long attractionId, Long dayPlanId) {
        if(attractionId == null || dayPlanId == null) {
            throw new IllegalArgumentException("Attraction id or day plan id is null");
        }
        var dayPlan = dayPlanService.getDayPlanById(dayPlanId);

        var toDelete = dayPlan.deleteAttraction(attractionId);
        toDelete.removeDay(dayPlan);

        if(toDelete.getDays().isEmpty()) {
            attractionRepository.delete(toDelete);
        }

        return toDelete;
    }

    @Transactional
    public Attraction addAttraction(List<Long> dayPlanIds, Long userId,  AttractionCandidateDto attractionCandidateDto) {
        if(dayPlanIds.isEmpty())
            throw new IllegalArgumentException("Day plan ids cannot be empty");

        if(userId == null)
            throw new IllegalArgumentException("User id cannot be null");

        var days = dayPlanService.getDayPlanById(dayPlanIds, userId);
        if(days.size() != dayPlanIds.size())
            throw new IllegalArgumentException("Day plan not found");

        var attraction = mapstructMapper.getAttractionFromCandidateDto(attractionCandidateDto);
        attraction.addDays(days);

        for (var day : days) {
            day.addAttraction(attraction);
        }

        return attractionRepository.save(attraction);
    }

    private List<AttractionCandidateDto> convertToAttractionCandidateDto(PlacesSearchResult[] foundCandidates) {
        if (foundCandidates == null) {
            return new ArrayList<>();
        }

        var result = new ArrayList<AttractionCandidateDto>();
        for (PlacesSearchResult foundCandidate : foundCandidates) {
            var candidate = new AttractionCandidateDto(foundCandidate.name,
                                                       foundCandidate.geometry.location.lat,
                                                       foundCandidate.geometry.location.lng,
                                                       foundCandidate.placeId,
                                                       foundCandidate.photos == null ? null : foundCandidate.photos[0].photoReference,
                                                       foundCandidate.formattedAddress);
            result.add(candidate);
        }
        return result;
    }

    private List<AttractionCandidateDto> getUrl(List<AttractionCandidateDto> candidates) {
        if (candidates == null) {
            return new ArrayList<>();
        }

        for (AttractionCandidateDto candidate : candidates) {
            try {
                var placeDetails = PlacesApi.placeDetails(context, candidate.getPlaceId())
                                            .fields(PlaceDetailsRequest.FieldMask.URL,
                                                    PlaceDetailsRequest.FieldMask.OPENING_HOURS)
                                            .await();
                candidate.setUrl(placeDetails.url.toString());
                candidate.setOpeningHours(placeDetails.openingHours == null ? null : placeDetails.openingHours.weekdayText);
            } catch (ApiException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return candidates;
    }

    public Attraction editAttraction(Long userId, Attraction attraction) {
        if(userId == null || attraction == null)
            throw new IllegalArgumentException("User id or attraction candidate dto is null");

        var toUpdate = attractionRepository.existsById(attraction.getAttraction_id());
        if(!toUpdate)
            throw new IllegalArgumentException("Attraction not found");

        return attractionRepository.save(attraction);
    }
}
