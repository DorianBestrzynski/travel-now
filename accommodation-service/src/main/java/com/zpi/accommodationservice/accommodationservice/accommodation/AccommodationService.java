package com.zpi.accommodationservice.accommodationservice.accommodation;

import com.zpi.accommodationservice.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.ApiPermissionException;
import com.zpi.accommodationservice.accommodationservice.exceptions.DataExtractionNotSupported;
import com.zpi.accommodationservice.accommodationservice.mapstruct.MapStructMapper;
import com.zpi.accommodationservice.accommodationservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.zpi.accommodationservice.accommodationservice.comons.Utils.SERVICE_REGEX;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.*;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final HashMap<String, AccommodationDataExtractionStrategy> extractionStrategies;
    private final TripGroupProxy tripGroupProxy;
    private final MapStructMapper mapstructMapper;

    @Transactional
    public Accommodation addAccommodation(AccommodationDto accommodationDto) {
        var isUserPartOfGroup = tripGroupProxy.isUserPartOfTheGroup(accommodationDto.groupId(), accommodationDto.creatorId());
        if (!isUserPartOfGroup)
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);

        var extractedData = extractDataFromUrl(accommodationDto.accommodationLink());
        var accommodation = new Accommodation(accommodationDto.groupId(), accommodationDto.creatorId(),
                                              extractedData.name(), extractedData.streetAddress(),
                                              extractedData.country(),
                                              extractedData.region(),
                                              accommodationDto.description(),
                                              extractedData.imageLink(),
                                              extractedData.url(), accommodationDto.price());

        return accommodationRepository.save(accommodation);
    }

    private AccommodationDataDto extractDataFromUrl(String bookingUrl) {
        var pattern = Pattern.compile(SERVICE_REGEX);
        var matcher = pattern.matcher(bookingUrl);

        String serviceName;
        if (matcher.find())
            serviceName = matcher.group();
        else
            throw new DataExtractionNotSupported(DATA_EXTRACTION_EXCEPTION);

        return extractionStrategies.get(serviceName)
                                   .extractDataFromUrl(bookingUrl);
    }

    public List<Accommodation> getAllAccommodationsForGroup(Long groupId, Long userId) {
        if(groupId == null || userId == null){
            throw new IllegalArgumentException(INVALID_GROUP_ID + " or " + INVALID_USER_ID);
        }
        var isUserPartOfGroup = tripGroupProxy.isUserPartOfTheGroup(groupId, userId);
        if (!isUserPartOfGroup)
            throw new ApiPermissionException(NOT_A_GROUP_MEMBER);

        return accommodationRepository.findAllByGroupId(groupId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

    }

    @Transactional
    public void deleteAccommodation(Long accommodationId, Long userId) {
        if(accommodationId == null || userId == null){
            throw new IllegalArgumentException(INVALID_ACCOMMODATION_ID + " or " + INVALID_USER_ID);
        }

        var accommodation = accommodationRepository.findById(accommodationId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if(hasEditingAccommodationPermissions(userId, accommodation)){
            accommodationRepository.deleteById(accommodationId);
        }
        else
            throw new ApiPermissionException(DELETING_PERMISSION_VIOLATION);

    }
    @Transactional
    public Accommodation editAccommodation(Long accommodationId, Long userId, AccommodationDto accommodationDto) {
        if(accommodationDto == null || userId == null ){
            throw new IllegalArgumentException(INVALID_ACCOMMODATION_ID + "or" + INVALID_USER_ID);
        }

        var accommodation =  accommodationRepository.findById(accommodationId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        if(hasEditingAccommodationPermissions(userId, accommodation)) {
            accommodationDto = mapstructMapper.adaptAccommodationDto(accommodationDto);
            mapstructMapper.updateFromAccommodationDtoToAccommodation(accommodation, accommodationDto);
            if(accommodationDto.accommodationLink() != null){
                var updatedAccommodation = extractDataFromUrl(accommodationDto.accommodationLink());
                mapstructMapper.updateFromAccommodationDataDtoToAccommodation(accommodation, updatedAccommodation);
            }
            accommodationRepository.save(accommodation);
            return accommodation;
        }
        throw new ApiPermissionException(EDITING_PERMISSION_VIOLATION);

    }

    private boolean hasEditingAccommodationPermissions(Long userId, Accommodation accommodation) {
        return tripGroupProxy.isUserPartOfTheGroup(accommodation.getGroupId(), userId) &&
                (tripGroupProxy.isUserCoordinator(accommodation.getGroupId(), userId) || userId.equals(accommodation.getCreator_id()));
    }


}
