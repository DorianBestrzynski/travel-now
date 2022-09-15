package com.zpi.accommodationservice.accommodationservice.accommodation;

import com.zpi.accommodationservice.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.DataExtractionNotSupported;
import com.zpi.accommodationservice.accommodationservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.regex.Pattern;

import static com.zpi.accommodationservice.accommodationservice.comons.Utils.SERVICE_REGEX;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.NOT_A_GROUP_MEMBER;

@Service
@RequiredArgsConstructor
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final HashMap<String, AccommodationDataExtractionStrategy> extractionStrategies;
    private final TripGroupProxy tripGroupProxy;

    @Transactional
    public Accommodation addAccommodation(AccommodationDto accommodationDto) {
        var isUserPartOfGroup = tripGroupProxy.isUserPartOfTheGroup(accommodationDto.groupId(), accommodationDto.creatorId());
        if (!isUserPartOfGroup)
            throw new IllegalArgumentException(NOT_A_GROUP_MEMBER);

        var extractedData = extractDataFromUrl(accommodationDto.accommodationLink());
        var accommodation = new Accommodation(accommodationDto.groupId(), accommodationDto.creatorId(),
                                              extractedData.name(), extractedData.address(),
                                              accommodationDto.description(),
                                              extractedData.imageUrl(),
                                              accommodationDto.accommodationLink(), accommodationDto.price());

        return accommodationRepository.save(accommodation);
    }

    private AccommodationDataDto extractDataFromUrl(String bookingUrl) {
        var pattern = Pattern.compile(SERVICE_REGEX);
        var matcher = pattern.matcher(bookingUrl);

        String serviceName;
        if (matcher.find())
            serviceName = matcher.group();
        else
            throw new DataExtractionNotSupported("Data extraction not supported for this service");

        return extractionStrategies.get(serviceName)
                                   .extractDataFromUrl(bookingUrl);
    }
}
