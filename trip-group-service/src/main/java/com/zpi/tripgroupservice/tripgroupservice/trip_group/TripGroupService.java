package com.zpi.tripgroupservice.tripgroupservice.trip_group;


import com.zpi.tripgroupservice.tripgroupservice.aspects.AuthorizeCoordinator;
import com.zpi.tripgroupservice.tripgroupservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.dto.AccommodationInfoDto;
import com.zpi.tripgroupservice.tripgroupservice.dto.AvailabilityConstraintsDto;
import com.zpi.tripgroupservice.tripgroupservice.dto.TripDataDto;
import com.zpi.tripgroupservice.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.tripgroupservice.google_api.Geolocation;
import com.zpi.tripgroupservice.tripgroupservice.mapper.MapStructMapper;
import com.zpi.tripgroupservice.tripgroupservice.proxy.AccommodationProxy;
import com.zpi.tripgroupservice.tripgroupservice.proxy.FinanceProxy;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.zpi.tripgroupservice.tripgroupservice.commons.Utils.LATITUDE_INDEX;
import static com.zpi.tripgroupservice.tripgroupservice.commons.Utils.LONGITUDE_INDEX;
import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.*;

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
            throw new IllegalArgumentException(INVALID_USER_ID);
        }
        return tripGroupRepository.findAllGroupsForUser(userId);
    }
    @AuthorizePartOfTheGroup
    public TripGroup getTripGroupById(Long groupId) {
        if (groupId == null || groupId < 0) {
            throw new IllegalArgumentException(INVALID_GROUP_ID);
        }
        return tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(GROUP_DOES_NOT_EXIST + groupId));
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
    public void deleteGroup(Long groupId, Long userId) {
        if(groupId == null || userId == null){
            throw new IllegalArgumentException(INVALID_GROUP_ID + "or" + INVALID_USER_ID);
        }
        tripGroupRepository.deleteById(groupId);
        userGroupService.deletionGroupCleanUp(groupId);
    }

   @Transactional
   @AuthorizeCoordinator
    public TripGroup updateGroup(Long groupId, Long userId, TripGroupDto tripGroupDto) {
        var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
        mapstructMapper.updateFromTripGroupDtoToTripGroup(tripGroup,tripGroupDto);
        tripGroupRepository.save(tripGroup);
        return tripGroup;
    }

    public TripDataDto getTripData(Long groupId) {
        return tripGroupRepository.findTripData(groupId);
    }


    public AvailabilityConstraintsDto getAvailabilityConstraints(Long groupId) {
        var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
        return new AvailabilityConstraintsDto(tripGroup.getMinimalNumberOfDays(), tripGroup.getMinimalNumberOfParticipants());
        }

    public AccommodationInfoDto getAccommodation(Long groupId) {
        if(groupId == null){
            throw new IllegalArgumentException(INVALID_GROUP_ID);
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
            throw new IllegalArgumentException(INVALID_GROUP_ID + "or" + INVALID_ACCOMMODATION_ID);

        var tripGroup = tripGroupRepository.findById(groupId)
                                           .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));

        if(accommodationProxy.getAccommodationInfo(INNER_COMMUNICATION,  accommodationId) == null)
            throw new ApiRequestException(ACCOMMODATION_NOT_FOUND);

        tripGroup.setSelectedAccommodationId(accommodationId);
        return tripGroup;
    }

    @Transactional
    @AuthorizeCoordinator
    public TripGroup setCurrencyInGroup(Long groupId, Long userId, Currency currency) {
        var tripGroup = tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
        tripGroup.setCurrency(currency);
        return tripGroupRepository.save(tripGroup);
    }

    @Transactional
    @AuthorizePartOfTheGroup
    public void leaveGroup(Long groupId, Long userId) {
        if(financeProxy.isDebtorOrDebteeToAnyFinancialRequests(INNER_COMMUNICATION,groupId, userId)){
            throw new ApiRequestException(CANNOT_LEAVE_GROUP);
        }
        userGroupService.deleteUserFromGroup(groupId, userId);
    }
}
