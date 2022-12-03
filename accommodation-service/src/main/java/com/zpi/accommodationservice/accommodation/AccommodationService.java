package com.zpi.accommodationservice.accommodation;

import com.zpi.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import com.zpi.accommodationservice.aspects.AuthorizeAuthorOrCoordinator;
import com.zpi.accommodationservice.aspects.AuthorizeCoordinator;
import com.zpi.accommodationservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.accommodationservice.comons.Utils;
import com.zpi.accommodationservice.dto.*;
import com.zpi.accommodationservice.exceptions.DataExtractionNotSupported;
import com.zpi.accommodationservice.exceptions.ExceptionsInfo;
import com.zpi.accommodationservice.mapstruct.MapStructMapper;
import com.zpi.accommodationservice.proxies.AppUserProxy;
import com.zpi.accommodationservice.proxies.TripGroupProxy;
import com.zpi.accommodationservice.votes.AccommodationVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    private final HashMap<String, AccommodationDataExtractionStrategy> extractionStrategies;

    private final TripGroupProxy tripGroupProxy;

    private final MapStructMapper mapstructMapper;

    private static final String INNER_COMMUNICATION = "microserviceCommunication";

    private final AccommodationVoteService accommodationVoteService;

    private final AppUserProxy appUserProxy;


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
            throw new DataExtractionNotSupported(ExceptionsInfo.DATA_EXTRACTION_EXCEPTION);

        return extractionStrategies.get(serviceName)
                                   .extractDataFromUrl(url);
    }

    @AuthorizePartOfTheGroup
    public List<Accommodation> getAllAccommodationsForGroup(Long groupId, Long userId) {
        if(groupId == null)
            throw new IllegalArgumentException(ExceptionsInfo.INVALID_GROUP_ID + " or " + ExceptionsInfo.INVALID_USER_ID);
        if(userId == null)
            return accommodationRepository.findAllByGroupId(groupId).orElseThrow(() -> new EntityNotFoundException(
                    ExceptionsInfo.ENTITY_NOT_FOUND));
        else
            return accommodationRepository.findAllByGroupIdAndCreatorId(groupId, userId).orElseThrow(() -> new EntityNotFoundException(
                    ExceptionsInfo.ENTITY_NOT_FOUND));
    }

    @AuthorizePartOfTheGroup
    public List<AccommodationWithVotesDto> getAllAccommodationsForGroupWithVotes(Long groupId) {
        if (groupId == null)
            throw new IllegalArgumentException(ExceptionsInfo.INVALID_GROUP_ID);

        var accommodations = accommodationRepository.findAllByGroupId(groupId)
                                                    .orElseThrow(() -> new EntityNotFoundException(ExceptionsInfo.ENTITY_NOT_FOUND));

        var votes = accommodationVoteService.getVotesForAccommodations(accommodations.stream()
                                                                                     .map(Accommodation::getAccommodationId)
                                                                                     .toList());

        var users = appUserProxy.getUsersDtos(INNER_COMMUNICATION, votes.parallelStream()
                                                                        .map(acc -> acc.getId().getUserId())
                                                                        .toList());


        var result = votes.parallelStream()
                          .collect(Collectors.groupingBy(
                                  vote -> findAccommodationById(accommodations, vote.getId().getAccommodationId()),
                                  Collectors.mapping(
                                          vote -> findUserById(users, vote.getId().getUserId()),
                                          Collectors.filtering(Objects::nonNull, Collectors.toList())
                                  ))
                          );

        if(result.size() == accommodations.size())
            return result.entrySet()
                         .parallelStream()
                         .map(e -> new AccommodationWithVotesDto(e.getKey(), e.getValue()))
                         .collect(Collectors.toList());

        return putMissingAccommodation(result, accommodations).entrySet()
                                                              .parallelStream()
                                                              .map(e -> new AccommodationWithVotesDto(e.getKey(), e.getValue()))
                                                              .collect(Collectors.toList());
    }

    private Accommodation findAccommodationById(List<Accommodation> accommodations, Long id) {
        return accommodations.parallelStream()
                             .filter(acc -> acc.getAccommodationId().equals(id))
                             .findAny()
                             .orElse(null);
    }

    private UserDto findUserById(List<UserDto> users, Long id) {
        return users.parallelStream()
                    .filter(acc -> acc.userId().equals(id))
                    .findAny()
                    .orElse(null);
    }

    private Map<Accommodation, List<UserDto>> putMissingAccommodation(Map<Accommodation, List<UserDto>> map, List<Accommodation> accommodations) {
        accommodations.forEach(acc -> map.putIfAbsent(acc, new ArrayList<>()));
        return map;
    }

    @Transactional
    @AuthorizeAuthorOrCoordinator
    public void deleteAccommodation(Long accommodationId) {
        if (accommodationId == null) {
            throw new IllegalArgumentException(
                    ExceptionsInfo.INVALID_ACCOMMODATION_ID + Utils.OR_WORD + ExceptionsInfo.INVALID_USER_ID);
        }
        accommodationRepository.deleteById(accommodationId);
    }

    @Transactional
    @AuthorizeAuthorOrCoordinator
    public Accommodation editAccommodation(Long accommodationId, Long userId, AccommodationDto accommodationDto) {
        if (accommodationDto == null || userId == null) {
            throw new IllegalArgumentException(
                    ExceptionsInfo.INVALID_ACCOMMODATION_ID + Utils.OR_WORD + ExceptionsInfo.INVALID_USER_ID);
        }

        var accommodation = accommodationRepository.findById(accommodationId).orElseThrow(() -> new EntityNotFoundException(
                ExceptionsInfo.ENTITY_NOT_FOUND));
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
       return accommodationRepository.getAccommodationInfoDto(accommodationId)
                                     .orElseThrow(() -> new EntityNotFoundException(ExceptionsInfo.ENTITY_NOT_FOUND));

    }

    @AuthorizeCoordinator
    public void acceptAccommodation(Long accommodationId) {
        var accommodation = accommodationRepository.findById(accommodationId).orElseThrow(() -> new EntityNotFoundException(ExceptionsInfo.ENTITY_NOT_FOUND));
        tripGroupProxy.setSelectedAccommodation(INNER_COMMUNICATION, accommodation.getGroupId(), accommodationId);

    }

    public Accommodation getAccommodation(Long accommodationId) {
        return accommodationRepository.findById(accommodationId).orElseThrow(() -> new EntityNotFoundException(ExceptionsInfo.ENTITY_NOT_FOUND));
    }

    public List<AccommodationWithVotesDto> getUserVotes(Long userId, Long groupId) {
        var votesForGroup = getAllAccommodationsForGroupWithVotes(groupId);

        return votesForGroup.parallelStream()
                            .filter(acc -> acc.userVoted()
                                              .parallelStream()
                                              .anyMatch(dto -> dto.userId().equals(userId)))
                            .collect(Collectors.toList());
    }
}
