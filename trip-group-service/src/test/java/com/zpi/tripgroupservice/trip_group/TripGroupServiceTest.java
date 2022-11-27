package com.zpi.tripgroupservice.trip_group;

import com.zpi.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.dto.*;
import com.zpi.tripgroupservice.exception.ApiPermissionException;
import com.zpi.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.google_api.Geolocation;
import com.zpi.tripgroupservice.mapper.MapStructMapper;
import com.zpi.tripgroupservice.proxy.AccommodationProxy;
import com.zpi.tripgroupservice.proxy.FinanceProxy;
import com.zpi.tripgroupservice.security.CustomUsernamePasswordAuthenticationToken;
import com.zpi.tripgroupservice.user_group.UserGroupService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class TripGroupServiceTest {
    @MockBean
    TripGroupRepository tripGroupRepository;

    @MockBean
    UserGroupService userGroupService;

    @Autowired
    MapStructMapper mapStructMapper;

    @MockBean
    Geolocation geolocation;

    @MockBean
    FinanceProxy financeProxy;

    @MockBean
    AccommodationProxy accommodationProxy;

    @Autowired
    @InjectMocks
    TripGroupService tripGroupService;

    void mockAuthorizePartOfTheGroupAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupService).checkIfUserIsInGroup(any(), any());
    }

    void mockAuthorizeCoordinatorAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupService).isUserCoordinator(any(), any());
    }

    void mockCustomUsernamePasswordAuthentication(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldGetAllGroupsForUser() {
        //given
        var groups = List.of(new TripGroup("Test", Currency.PLN, "Desc", 1, "Raclawicka",
                                           "Wroclaw" , 3, 3 ));

        //when
        when(tripGroupRepository.findAllGroupsForUser(anyLong())).thenReturn(groups);
        var actualResult = tripGroupService.getAllGroupsForUser(1L);

        //then
        verify(tripGroupRepository, times(1)).findAllGroupsForUser(anyLong());
        assertThat(actualResult).hasSameElementsAs(groups);
    }

    @Test
    void shouldThrowErrorWhenUserIdInvalidGetAllGroupsById() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> tripGroupService.getAllGroupsForUser(null));

        //then
        verify(tripGroupRepository, never()).findAllGroupsForUser(anyLong());
        assertThat(exception.getMessage()).isEqualTo("User id is invalid. Id must be a positive number");
    }

    @Test
    void shouldReturnTripGroupById() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var group = new TripGroup("Test", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(group));
        var result = tripGroupService.getTripGroupById(1L);

        //then
        verify(tripGroupRepository, times(1)).findById(anyLong());
        assertThat(result).isEqualTo(group);
    }

    @Test
    void shouldThrowErrorWhenGroupIdInvalidGetGroupById() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> tripGroupService.getTripGroupById(-2L));

        //then
        verify(tripGroupRepository, never()).findById(anyLong());
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number");
    }

    @Test
    void shouldThrowErrorWhenTripGroupIsNotFound() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                                     () -> tripGroupService.getTripGroupById(1L));

        //then
        verify(tripGroupRepository, times(1)).findById(anyLong());
        assertThat(exception.getMessage()).isEqualTo("There are no group for given id: " + 1L);
    }

    @Test
    void shouldSuccessfullyCreateGroup() {
        mockCustomUsernamePasswordAuthentication();
        //given
        var tripGroupDto = new TripGroupDto("Name", Currency.PLN, "Desc", 1,
                                            "Raclawicka", "Wroclaw", 1, 1);
        Double[] coordinates = { 11.22, 22.33 };

        //when
        when(geolocation.findCoordinates(anyString())).thenReturn(coordinates);
        var actualTripGroup = tripGroupService.createGroup(tripGroupDto);

        //then
        var expectedTripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 1, 1 );
        expectedTripGroup.setLatitude(11.22);
        expectedTripGroup.setLongitude(22.33);
        assertThat(actualTripGroup).isEqualTo(expectedTripGroup);
        verify(geolocation, times(1)).findCoordinates(anyString());
        verify(tripGroupRepository, times(1)).save(any());
        verify(userGroupService, times(1)).createUserGroup(anyLong(), any(), anyInt());
    }

    @Test
    void shouldSuccessfullyDeleteGroup() {
        //given
        mockAuthorizeCoordinatorAspect();

        //when
        tripGroupService.deleteGroup(1L);

        //then
        verify(tripGroupRepository, times(1)).deleteById(1L);
        verify(userGroupService, times(1)).deletionGroupCleanUp(1L);
    }

    @Test
    void shouldThrowErrorWhenUserIdOrGroupIdIsInvalidInDeleteGroup() {
        //given
        mockAuthorizeCoordinatorAspect();

        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> tripGroupService.deleteGroup(null));

        //then
        verify(tripGroupRepository, never()).deleteById(anyLong());
        verify(userGroupService, never()).deletionGroupCleanUp(anyLong());
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number");
    }

    @Test
    void shouldCorrectlyUpdateTripGroup() {
        //given
        mockAuthorizeCoordinatorAspect();
        var tripGroupDto = new TripGroupDto("Name", Currency.EURO, "Updated Desc", 1,
                "Raclawicka", "China", 1, 1);
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 1, 2 );

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        var actualTripGroup = tripGroupService.updateGroup(1L, tripGroupDto);

        //then
        var expectedTripGroup = new TripGroup("Name", Currency.EURO, "Updated Desc", 1, "Raclawicka",
                "China" , 1, 1 );
        assertThat(actualTripGroup).isEqualTo(expectedTripGroup);
        verify(tripGroupRepository, times(1)).save(any());
        verify(tripGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowErrorWhenTripGroupNotFoundUpdateGroup() {
        //given
        mockAuthorizeCoordinatorAspect();

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                () -> tripGroupService.updateGroup(1L, null));

        //then
        assertThat(exception.getMessage()).isEqualTo("There is no group with given group_id ");
        verify(tripGroupRepository, never()).save(any());
        verify(tripGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetTripData() {
        //given
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );
        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        when(userGroupService.getNumberOfParticipants(anyLong())).thenReturn(2);
        var actualResult = tripGroupService.getTripData(1L);

        //then
        var expectedResult = new TripExtendedDataDto(tripGroup.getName(), tripGroup.getCurrency(), tripGroup.getDescription(),
                tripGroup.getVotesLimit(), tripGroup.getStartLocation(), tripGroup.getStartCity(), tripGroup.getStartDate(),
                tripGroup.getEndDate(), tripGroup.getLatitude(), tripGroup.getLongitude(), tripGroup.getGroupStage(),
                tripGroup.getMinimalNumberOfDays(), tripGroup.getMinimalNumberOfParticipants(), tripGroup.getSelectedAccommodationId(),
                tripGroup.getSelectedSharedAvailability(), 2);
        assertThat(actualResult).isEqualTo(expectedResult);
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(userGroupService, times(1)).getNumberOfParticipants(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenGroupNotFound() {
        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                () -> tripGroupService.getTripData(1L));

        //then
        assertThat(exception.getMessage()).isEqualTo("There is no group with given group_id ");
        verify(tripGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldReturnAvailabilityConstraints() {
        //given
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        var actualResult = tripGroupService.getAvailabilityConstraints(1L);

        //then
        var expectedResult = new AvailabilityConstraintsDto(3, 3);
        assertThat(actualResult).isEqualTo(expectedResult);
        verify(tripGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowErrorWhenTripGroupNotFoundAvailabilityConstraint() {
        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                () -> tripGroupService.getAvailabilityConstraints(1L));

        //then
        assertThat(exception.getMessage()).isEqualTo("There is no group with given group_id ");
        verify(tripGroupRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldSuccessfullyGetAccommodation() {
        //given
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );
        tripGroup.setSelectedAccommodationId(1L);

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        tripGroupService.getAccommodation(1L);

        //then
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(accommodationProxy, times(1)).getAccommodationInfo(anyString(), anyLong());
    }

    @Test
    void shouldReturnEmptyAccommodation() {
        //given
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );
        tripGroup.setSelectedAccommodationId(null);

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        var actualResult = tripGroupService.getAccommodation(1L);

        //then
        var expectedResult = new AccommodationInfoDto();
        assertThat(actualResult).isEqualTo(expectedResult);
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(accommodationProxy, never()).getAccommodationInfo(anyString(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenGroupIdNotValid() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> tripGroupService.getAccommodation(null));

        //then
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number");
        verify(tripGroupRepository, never()).findById(anyLong());
        verify(accommodationProxy, never()).getAccommodationInfo(anyString(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenTripGroupNotFoundGetAccommodation() {
        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                () -> tripGroupService.getAccommodation(1L));

        //then
        assertThat(exception.getMessage()).isEqualTo("There is no group with given group_id ");
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(accommodationProxy, never()).getAccommodationInfo(anyString(), anyLong());
    }


    @Test
    void shouldSuccessfullySetAccommodation() {
        //given
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        when(tripGroupRepository.save(any(TripGroup.class))).thenAnswer(s -> s.getArguments()[0]);

        var actualResult = tripGroupService.setSelectedAccommodation(1L, 1L);
        //then
        var expectedResult = new TripGroup();
        expectedResult.setSelectedAccommodationId(1L);
        assertThat(actualResult.getSelectedAccommodationId()).isEqualTo(expectedResult.getSelectedAccommodationId());
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(tripGroupRepository, times(1)).save(any(TripGroup.class));
    }

    @Test
    void shouldThrowExceptionWhenGroupIdOrAccommodationIdNotValid() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> tripGroupService.setSelectedAccommodation(1L, null));

        //then
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number" + "or"
                + "Invalid accommodation id");
        verify(tripGroupRepository, never()).findById(anyLong());
        verify(accommodationProxy, never()).getAccommodationInfo(anyString(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenTripGroupNotFoundSetAccommodation() {
        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                () -> tripGroupService.setSelectedAccommodation(1L, 1L));

        //then
        assertThat(exception.getMessage()).isEqualTo("There is no group with given group_id ");
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(accommodationProxy, never()).getAccommodationInfo(anyString(), anyLong());
    }

    @Test
    void shouldCorrectlySetCurrencyInGroup() {
        //given
        mockAuthorizeCoordinatorAspect();
        var currency = Currency.EURO;
        var tripGroup = new TripGroup("Name", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.of(tripGroup));
        when(tripGroupRepository.save(any(TripGroup.class))).thenAnswer(i -> i.getArguments()[0]);
        var actualResult = tripGroupService.setCurrencyInGroup(1L, currency);

        //then
        assertThat(actualResult.getCurrency()).isEqualTo(currency);
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(tripGroupRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowErrorWhenTripGroupNotFoundSetCurrency() {
        //given
        mockAuthorizeCoordinatorAspect();

        //when
        when(tripGroupRepository.findById(anyLong())).thenReturn(Optional.empty());
        var exception = assertThrows(ApiRequestException.class,
                () -> tripGroupService.setCurrencyInGroup(1L, Currency.PLN));

        //then
        assertThat(exception.getMessage()).isEqualTo("There is no group with given group_id ");
        verify(tripGroupRepository, times(1)).findById(anyLong());
        verify(tripGroupRepository, never()).save(any());
    }

    @Test
    void shouldBeAbleToLeaveGroup() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(financeProxy.isDebtorOrDebteeToAnyFinancialRequests(anyString(), anyLong(), anyLong())).thenReturn(false);
        tripGroupService.leaveGroup(1L, 1L);

        //then
        verify(financeProxy, times(1)).isDebtorOrDebteeToAnyFinancialRequests(anyString(), anyLong(), anyLong());
        verify(userGroupService, times(1)).deleteUserFromGroup(anyLong(), anyLong());
    }

    @Test
    void shouldThrowErrorWhenUserHasUnsettledFinancialRequests() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(financeProxy.isDebtorOrDebteeToAnyFinancialRequests(anyString(), anyLong(), anyLong())).thenReturn(true);
        var exception = assertThrows(ApiPermissionException.class,
                () -> tripGroupService.leaveGroup(1L, 1L));

        //then
        assertThat(exception.getMessage()).isEqualTo("You cannot leave group if you have unsettled expenses");
        verify(financeProxy, times(1)).isDebtorOrDebteeToAnyFinancialRequests(anyString(), anyLong(), anyLong());
        verify(userGroupService, never()).deleteUserFromGroup(anyLong(), anyLong());
    }
}