package com.zpi.tripgroupservice.trip_group;


import com.zpi.tripgroupservice.aspects.AuthorizeCoordinator;
import com.zpi.tripgroupservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.dto.AccommodationInfoDto;
import com.zpi.tripgroupservice.dto.AvailabilityConstraintsDto;
import com.zpi.tripgroupservice.dto.TripExtendedDataDto;
import com.zpi.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.exception.ExceptionInfo;
import com.zpi.tripgroupservice.google_api.Geolocation;
import com.zpi.tripgroupservice.mapper.MapStructMapper;
import com.zpi.tripgroupservice.proxy.AccommodationProxy;
import com.zpi.tripgroupservice.proxy.FinanceProxy;
import com.zpi.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.zpi.tripgroupservice.commons.Utils.LATITUDE_INDEX;
import static com.zpi.tripgroupservice.commons.Utils.LONGITUDE_INDEX;
import static com.zpi.tripgroupservice.exception.ExceptionInfo.GROUP_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TripGroupService {
    private final TripGroupRepository tripGroupRepository;
    private final UserGroupService userGroupService;
    private final MapStructMapper mapstructMapper;
    private final Geolocation geolocation;
    private final FinanceProxy financeProxy;
    private final AccommodationProxy accommodationProxy;
    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    public List<TripGroup> getAllGroupsForUser(Long userId){
        if(userId == null){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_USER_ID);
        }
        return tripGroupRepository.findAllGroupsForUser(userId);
    }
    @AuthorizePartOfTheGroup
    public TripGroup getTripGroupById(Long groupId) {
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException(ExceptionInfo.INVALID_GROUP_ID);
        }
        return tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(ExceptionInfo.GROUP_DOES_NOT_EXIST + groupId));
    }
    @Transactional
    public TripGroup createGroup(Long userId, TripGroupDto groupDto) {
            var tripGroup = new TripGroup(groupDto.name(),groupDto.currency(),groupDto.description(), groupDto.votesLimit(),
                                          groupDto.startLocation(), groupDto.startCity(),
                                          groupDto.minimalNumberOfDays(), groupDto.minimalNumberOfParticipants());
            var coordinates = geolocation.findCoordinates(groupDto.startLocation());
            tripGroup.setLatitude(coordinates[LATITUDE_INDEX]);
            tripGroup.setLongitude(coordinates[LONGITUDE_INDEX]);
            tripGroupRepository.save(tripGroup);
            userGroupService.createUserGroup(userId, tripGroup.getGroupId(), tripGroup.getVotesLimit());
            return tripGroup;
    }


    @Transactional
    @AuthorizeCoordinator
    public void deleteGroup(Long groupId) {
        if(groupId == null){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_GROUP_ID);
        }
        tripGroupRepository.deleteById(groupId);
        userGroupService.deletionGroupCleanUp(groupId);
    }

   @Transactional
   @AuthorizeCoordinator
    public TripGroup updateGroup(Long groupId, TripGroupDto tripGroupDto) {
        var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(
                ExceptionInfo.GROUP_NOT_FOUND));
        mapstructMapper.updateFromTripGroupDtoToTripGroup(tripGroup,tripGroupDto);
        tripGroupRepository.save(tripGroup);
        return tripGroup;
    }

    public TripExtendedDataDto getTripData(Long groupId) {
        var group = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(
                GROUP_NOT_FOUND));
        var numOfParticipants = userGroupService.getNumberOfParticipants(groupId);
        return TripExtendedDataDto.builder()
                .name(group.getName())
                .currency(group.getCurrency())
                .description(group.getDescription())
                .votesLimit(group.getVotesLimit())
                .startLocation(group.getStartLocation())
                .startCity(group.getStartCity())
                .startDate(group.getStartDate())
                .endDate(group.getEndDate())
                .latitude(group.getLatitude())
                .longitude(group.getLongitude())
                .groupStage(group.getGroupStage())
                .minimalNumberOfDays(group.getMinimalNumberOfDays())
                .minimalNumberOfParticipants(group.getMinimalNumberOfParticipants())
                .selectedAccommodationId(group.getSelectedAccommodationId())
                .participantsNum(numOfParticipants)
                .build();
    }


    public AvailabilityConstraintsDto getAvailabilityConstraints(Long groupId) {
        var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(
                GROUP_NOT_FOUND));
        return new AvailabilityConstraintsDto(tripGroup.getMinimalNumberOfDays(), tripGroup.getMinimalNumberOfParticipants());
        }

    public AccommodationInfoDto getAccommodation(Long groupId) {
        if(groupId == null || groupId < 0){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_GROUP_ID);
        }

        var tripGroup = tripGroupRepository.findById(groupId)
                                           .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));

        if(tripGroup.getSelectedAccommodationId() != null)
            return accommodationProxy.getAccommodationInfo(INNER_COMMUNICATION, tripGroup.getSelectedAccommodationId());

        return new AccommodationInfoDto();
    }

    @Transactional
    public TripGroup setSelectedAccommodation(Long groupId, Long accommodationId) {
        if(groupId == null || accommodationId == null)
            throw new IllegalArgumentException(
                    ExceptionInfo.INVALID_GROUP_ID + "or" + ExceptionInfo.INVALID_ACCOMMODATION_ID);

        var tripGroup = tripGroupRepository.findById(groupId)
                                           .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));

        if(accommodationProxy.getAccommodationInfo(INNER_COMMUNICATION,  accommodationId) == null)
            throw new ApiRequestException(ExceptionInfo.ACCOMMODATION_NOT_FOUND);

        tripGroup.setSelectedAccommodationId(accommodationId);
        return tripGroup;
    }

    @Transactional
    @AuthorizeCoordinator
    public TripGroup setCurrencyInGroup(Long groupId, Currency currency) {
        var tripGroup = tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
        tripGroup.setCurrency(currency);
        return tripGroupRepository.save(tripGroup);
    }

    @Transactional
    @AuthorizePartOfTheGroup
    public void leaveGroup(Long groupId, Long userId) {
        if(financeProxy.isDebtorOrDebteeToAnyFinancialRequests(INNER_COMMUNICATION, groupId, userId)){
            throw new ApiRequestException(ExceptionInfo.CANNOT_LEAVE_GROUP);
        }
        userGroupService.deleteUserFromGroup(groupId, userId);
    }
}
