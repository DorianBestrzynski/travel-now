package com.zpi.accommodationservice.accommodationservice.accommodation;

import com.zpi.accommodationservice.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import com.zpi.accommodationservice.accommodationservice.aspects.AuthorizeAuthorOrCoordinator;
import com.zpi.accommodationservice.accommodationservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationInfoDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.DataExtractionNotSupported;
import com.zpi.accommodationservice.accommodationservice.mapstruct.MapStructMapper;
import com.zpi.accommodationservice.accommodationservice.proxies.UserGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.zpi.accommodationservice.accommodationservice.comons.Utils.OR_WORD;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.*;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    private final HashMap<String, AccommodationDataExtractionStrategy> extractionStrategies;

    private final UserGroupProxy userGroupProxy;

    private final MapStructMapper mapstructMapper;

    @Qualifier("serviceRegexPattern")
    private final Pattern pattern;

    @Transactional
    @AuthorizePartOfTheGroup
    public Accommodation addAccommodation(AccommodationDto accommodationDto) {
        var extractedData = extractDataFromUrl(accommodationDto.accommodationLink());
        var accommodation = new Accommodation(accommodationDto.groupId(), accommodationDto.creatorId(),
                                              extractedData.name(), extractedData.streetAddress(),
                                              extractedData.city(),
                                              extractedData.country(),
                                              extractedData.region(),
                                              accommodationDto.description(),
                                              extractedData.imageLink(),
                                              extractedData.sourceLink(), accommodationDto.price(),
                                              extractedData.latitude(),
                                              extractedData.longitude());

        return accommodationRepository.save(accommodation);
    }


    private AccommodationDataDto extractDataFromUrl(String url) {
        var matcher = pattern.matcher(url);

        String serviceName;
        if (matcher.find())
            serviceName = matcher.group();
        else
            throw new DataExtractionNotSupported(DATA_EXTRACTION_EXCEPTION);

        return extractionStrategies.get(serviceName)
                                   .extractDataFromUrl(url);
    }

    @AuthorizePartOfTheGroup
    public List<Accommodation> getAllAccommodationsForGroup(Long groupId) {
        if(groupId == null)
            throw new IllegalArgumentException(INVALID_GROUP_ID + " or " + INVALID_USER_ID);
        return accommodationRepository.findAllByGroupId(groupId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    @Transactional
    @AuthorizeAuthorOrCoordinator
    public void deleteAccommodation(Long accommodationId) {
        if(accommodationId == null){
            throw new IllegalArgumentException(INVALID_ACCOMMODATION_ID + OR_WORD + INVALID_USER_ID);
        }
        accommodationRepository.deleteById(accommodationId);
    }

    @Transactional
    @AuthorizeAuthorOrCoordinator
    public Accommodation editAccommodation(Long accommodationId, Long userId, AccommodationDto accommodationDto) {
        if (accommodationDto == null || userId == null) {
            throw new IllegalArgumentException(INVALID_ACCOMMODATION_ID + OR_WORD + INVALID_USER_ID);
        }

        var accommodation = accommodationRepository.findById(accommodationId).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        accommodationDto = mapstructMapper.adaptAccommodationDto(accommodationDto);
        mapstructMapper.updateFromAccommodationDtoToAccommodation(accommodation, accommodationDto);
        if (accommodationDto.accommodationLink() != null) {
            var updatedAccommodation = extractDataFromUrl(accommodationDto.accommodationLink());
            mapstructMapper.updateFromAccommodationDataDtoToAccommodation(accommodation, updatedAccommodation);
        }
        accommodationRepository.save(accommodation);
        return accommodation;

    }

    public AccommodationInfoDto getAccommodationInfo(Long accommodationId) {
        return accommodationRepository.getAccommodationInfoDto(accommodationId);
    }
}
