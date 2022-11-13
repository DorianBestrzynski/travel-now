package com.zpi.tripgroupservice.tripgroupservice.invitation;

import com.zpi.tripgroupservice.tripgroupservice.commons.Currency;
import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.tripgroupservice.security.CustomUsernamePasswordAuthenticationToken;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroupService;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroup;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupKey;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupRepository;
import com.zpi.tripgroupservice.tripgroupservice.user_group.UserGroupService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class InvitationServiceTest {
    @MockBean
    InvitationRepository invitationRepository;

    @MockBean
    UserGroupService userGroupService;

    @MockBean
    TripGroupService tripGroupService;

    @Autowired
    @InjectMocks
    InvitationService invitationService;

    @Captor
    ArgumentCaptor<Invitation> invitationArgumentCaptor;

    @Captor
    ArgumentCaptor<UserGroupKey> userGroupKeyArgumentCaptor;

    void mockAuthorizeCoordinatorAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupService).isUserCoordinator(anyLong(), anyLong());
    }

    @Test
    void shouldCorrectlyCreateInvitation() {
        //given
        mockAuthorizeCoordinatorAspect();
        var tripGroup = new TripGroup("Test", Currency.PLN, "Desc", 1, "Raclawicka",
                "Wroclaw" , 3, 3 );

        //when
        when(tripGroupService.getTripGroupById(1L)).thenReturn(tripGroup);
        var result = invitationService.createInvitation(1L, 1L);

        //then
        verify(invitationRepository, times(1)).save(invitationArgumentCaptor.capture());
        assertThat(result)
                .isEqualTo("http://localhost:8080/api/v1/invitation/?token=" + invitationArgumentCaptor.getValue().getInvitationId());
    }

    @Test
    void shouldThrowErrorWhenUserIdOrGroupIdIsInvalid() {
        //given
        mockAuthorizeCoordinatorAspect();

        //when
        var exception = assertThrows(IllegalArgumentException.class ,
                () ->  invitationService.createInvitation(-1L, 1L));

        //then
        verify(invitationRepository, never()).save(any());
        verify(tripGroupService, never()).getTripGroupById(anyLong());
        assertThat(exception.getMessage()).isEqualTo("User id and group id must be positive");
    }

    @Test
    void shouldAcceptInvitation() {
        //given
        String token = "token";
        Long userId = 1L;
        var tripGroup = new TripGroup();
        tripGroup.setGroupId(1L);
        var invitation = new Invitation(token, tripGroup);
        var userGroupKey = new UserGroupKey(userId, tripGroup.getGroupId());
        var expectedResult = new UserGroup(userGroupKey, Role.PARTICIPANT, 1);

        //when
        when(invitationRepository.findById(token)).thenReturn(Optional.of(invitation));
        when(userGroupService.exists(userGroupKey)).thenReturn(false);
        when(userGroupService.createUserGroup(any(UserGroupKey.class), any(Role.class), anyInt())).thenReturn(expectedResult);
        invitationService.acceptInvitation(userId, token);

        //then
        verify(invitationRepository, times(1)).findById(anyString());
        verify(userGroupService, times(1)).exists(userGroupKeyArgumentCaptor.capture());
        var actualKey = userGroupKeyArgumentCaptor.getValue();
        assertThat(expectedResult.getId()).satisfies(
                uk -> {
                    assertThat(uk.getGroupId()).isEqualTo(actualKey.getGroupId());
                    assertThat(uk.getUserId()).isEqualTo(actualKey.getUserId());
                }
        );
    }

    @Test
    void shouldThrowErrorWhenInvalidInvitationToken() {
        //given
        String token = "token";
        Long userId = 1L;

        //when
        when(invitationRepository.findById(anyString())).thenReturn(Optional.empty());
        var exception = assertThrows(IllegalArgumentException.class,
                () -> invitationService.acceptInvitation(userId, token));

        //then
        verify(invitationRepository, times(1)).findById(anyString());
        verify(userGroupService, never()).exists(any());
        assertThat(exception.getMessage()).isEqualTo("Invalid invitation token");
    }

    @Test
    void shouldThrowErrorWhenUserIsAlreadyMember() {
        //given
        String token = "token";
        Long userId = 1L;
        var tripGroup = new TripGroup();
        tripGroup.setGroupId(1L);
        var invitation = new Invitation(token, tripGroup);

        //when
        when(invitationRepository.findById(token)).thenReturn(Optional.of(invitation));
        when(userGroupService.exists(any())).thenReturn(true);
        var exception = assertThrows(IllegalArgumentException.class,
                () -> invitationService.acceptInvitation(userId, token));

        //then
        verify(invitationRepository, times(1)).findById(anyString());
        verify(userGroupService, times(1)).exists(any());
        assertThat(exception.getMessage()).isEqualTo("User is already a member of the group");
    }

}