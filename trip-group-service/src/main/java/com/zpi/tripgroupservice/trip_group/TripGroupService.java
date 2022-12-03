package com.zpi.tripgroupservice.trip_group;


import com.zpi.tripgroupservice.aspects.AuthorizeCoordinator;
import com.zpi.tripgroupservice.aspects.AuthorizePartOfTheGroup;
import com.zpi.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.commons.GroupStage;
import com.zpi.tripgroupservice.dto.*;
import com.zpi.tripgroupservice.exception.ApiPermissionException;
import com.zpi.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.exception.ExceptionInfo;
import com.zpi.tripgroupservice.google_api.Geolocation;
import com.zpi.tripgroupservice.mapper.MapStructMapper;
import com.zpi.tripgroupservice.proxy.AccommodationProxy;
import com.zpi.tripgroupservice.proxy.AvailabilityProxy;
import com.zpi.tripgroupservice.proxy.FinanceProxy;
import com.zpi.tripgroupservice.security.CustomUsernamePasswordAuthenticationToken;
import com.zpi.tripgroupservice.user_group.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
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
    private final AvailabilityProxy availabilityProxy;
    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    public List<TripExtendedDataDto> getAllGroupsForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(ExceptionInfo.INVALID_USER_ID);
        }
        var tripGroups = tripGroupRepository.findAllGroupsForUser(userId);
        return tripGroups.stream()
                .map(group -> new TripExtendedDataDto(
                        group.getGroupId(),
                        group.getName(),
                        group.getCurrency(),
                        group.getDescription(),
                        group.getVotesLimit(),
                        group.getStartLocation(),
                        group.getStartCity(),
                        group.getStartDate(),
                        group.getEndDate(),
                        group.getLatitude(),
                        group.getLongitude(),
                        group.getGroupStage(),
                        group.getMinimalNumberOfDays(),
                        group.getMinimalNumberOfParticipants(),
                        group.getSelectedAccommodationId(),
                        group.getSelectedSharedAvailability(),
                        userGroupService.getNumberOfParticipants(group.getGroupId())
                )).toList();
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
    public TripGroup createGroup(TripGroupDto groupDto) {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var tripGroup = new TripGroup(groupDto.name(),groupDto.currency(),groupDto.description(), groupDto.votesLimit(),
                                          groupDto.startLocation(), groupDto.startCity(),
                                          groupDto.minimalNumberOfDays(), groupDto.minimalNumberOfParticipants());
            var coordinates = geolocation.findCoordinates(groupDto.startLocation());
            tripGroup.setLatitude(coordinates[LATITUDE_INDEX]);
            tripGroup.setLongitude(coordinates[LONGITUDE_INDEX]);
            tripGroupRepository.save(tripGroup);
            userGroupService.createUserGroup(authentication.getUserId(), tripGroup.getGroupId(), tripGroup.getVotesLimit());
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
        return new TripExtendedDataDto(
                groupId,
                group.getName(),
                group.getCurrency(),
                group.getDescription(),
                group.getVotesLimit(),
                group.getStartLocation(),
                group.getStartCity(),
                group.getStartDate(),
                group.getEndDate(),
                group.getLatitude(),
                group.getLongitude(),
                group.getGroupStage(),
                group.getMinimalNumberOfDays(),
                group.getMinimalNumberOfParticipants(),
                group.getSelectedAccommodationId(),
                group.getSelectedSharedAvailability(),
                numOfParticipants);
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

    public AccommodationDto getAccommodationDto(Long groupId) {
        if(groupId == null || groupId < 0){
            throw new IllegalArgumentException(ExceptionInfo.INVALID_GROUP_ID);
        }

        var tripGroup = tripGroupRepository.findById(groupId)
                                           .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));

        if(tripGroup.getSelectedAccommodationId() != null)
            return accommodationProxy.getAccommodation(INNER_COMMUNICATION, tripGroup.getSelectedAccommodationId());

        return new AccommodationDto();
    }

    @Transactional
    public TripGroup setSelectedAccommodation(Long groupId, Long accommodationId) {
        if(groupId == null || accommodationId == null)
            throw new IllegalArgumentException(
                    ExceptionInfo.INVALID_GROUP_ID + "or" + ExceptionInfo.INVALID_ACCOMMODATION_ID);

        var tripGroup = tripGroupRepository.findById(groupId)
                                           .orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));

        tripGroup.setSelectedAccommodationId(accommodationId);
        return tripGroupRepository.save(tripGroup);
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
    public void leaveGroup(Long groupId) {
        var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        deletingUserCleanUp(authentication.getUserId(), groupId, tripGroup.getGroupStage());
        userGroupService.deleteUserFromGroup(groupId, authentication.getUserId());
    }

    private void afterPlanningStageCheck(Long groupId, Long userId) {
        if (financeProxy.isDebtorOrDebteeToAnyFinancialRequests(INNER_COMMUNICATION, groupId, userId)) {
            throw new ApiPermissionException(ExceptionInfo.CANNOT_LEAVE_GROUP);
        }
        if (userGroupService.getAllCoordinatorsIdsInGroup(groupId).size() == 1 && userGroupService.isUserCoordinator(userId, groupId)) {
            throw new ApiPermissionException(ExceptionInfo.LAST_COORDINATOR);
        }
    }

    @Transactional
    public void setSelectedAvailability(Long groupId, Long availabilityId, LocalDate startDate, LocalDate endDate) {
        var tripGroup = tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(ExceptionInfo.GROUP_NOT_FOUND));

        tripGroup.setSelectedSharedAvailability(availabilityId);
        tripGroup.setStartDate(startDate);
        tripGroup.setEndDate(endDate);
        tripGroupRepository.save(tripGroup);
    }

    @Transactional
    public void changeGroupStage(Long groupId) {
        var tripGroup = tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(ExceptionInfo.GROUP_NOT_FOUND));
        var tripGroupStage = tripGroup.getGroupStage();
        switch (tripGroupStage) {
            case PLANNING_STAGE -> tripGroup.setGroupStage(GroupStage.TRIP_STAGE);
            case TRIP_STAGE, AFTER_TRIP_STAGE -> tripGroup.setGroupStage(GroupStage.AFTER_TRIP_STAGE);
        }
        tripGroupRepository.save(tripGroup);
    }

    @Transactional
    public void unselectAvailability(Long groupId) {
        var tripGroup = tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(ExceptionInfo.GROUP_NOT_FOUND));
        tripGroup.setSelectedSharedAvailability(null);
        tripGroupRepository.save(tripGroup);
    }

    @Transactional
    public void unselectAccommodation(Long groupId) {
        var tripGroup = tripGroupRepository.findById(groupId)
                .orElseThrow(() -> new ApiRequestException(ExceptionInfo.GROUP_NOT_FOUND));
        tripGroup.setSelectedAccommodationId(null);
        tripGroupRepository.save(tripGroup);
    }

    public TripDataDto getTripDataForTransport(Long groupId) {
        return tripGroupRepository.findTripData(groupId).orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
    }

    @Transactional
    @AuthorizeCoordinator
    public void deleteUserFromGroup(Long groupId, Long userId) {
        var tripGroup = tripGroupRepository.findById(groupId).orElseThrow(() -> new ApiRequestException(GROUP_NOT_FOUND));
        deletingUserCleanUp(userId, groupId, tripGroup.getGroupStage());
        userGroupService.deleteUserFromGroup(groupId, userId);
    }

    public void deletingUserCleanUp(Long userId, Long groupId, GroupStage groupStage) {
        switch (groupStage) {
            case PLANNING_STAGE -> planningStageCleanUp(userId, groupId);
            case TRIP_STAGE, AFTER_TRIP_STAGE -> afterPlanningStageCheck(groupId, userId);
        }
    }

    private void planningStageCleanUp(Long userId, Long groupId) {
        if (userGroupService.getAllCoordinatorsIdsInGroup(groupId).size() == 1 && userGroupService.isUserCoordinator(userId, groupId)) {
            throw new ApiPermissionException(ExceptionInfo.LAST_COORDINATOR);
        }
        availabilityProxy.deleteAllAvailabilitiesForUser(INNER_COMMUNICATION, userId, groupId);
        accommodationProxy.deleteAllVotesForUserInGivenGroup(INNER_COMMUNICATION, userId, groupId);
    }
}
