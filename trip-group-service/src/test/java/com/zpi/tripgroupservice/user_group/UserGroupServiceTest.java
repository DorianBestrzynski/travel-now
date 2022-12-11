package com.zpi.tripgroupservice.user_group;

import com.zpi.tripgroupservice.commons.Role;
import com.zpi.tripgroupservice.exception.ApiRequestException;
import com.zpi.tripgroupservice.proxy.AppUserProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserGroupServiceTest {
    @MockBean
    UserGroupRepository userGroupRepository;

    @MockBean
    AppUserProxy appUserProxy;

    @Autowired
    @InjectMocks
    UserGroupService userGroupService;

    @Test
    void shouldSuccessfullyCreateUserGroupWithCoordinatorRole() {
        //given
        var expectedResult = new UserGroup(new UserGroupKey(1L,1L), Role.COORDINATOR, 1);

        //when
        when(userGroupRepository.save(any(UserGroup.class))).thenAnswer(i -> i.getArguments()[0]);
        var result = userGroupService.createUserGroup(1L, 1L, 1);

        //then
        assertThat(result).satisfies(
                ug -> {
                    assertThat(ug.getId().getUserId()).isEqualTo(expectedResult.getId().getUserId());
                    assertThat(ug.getId().getGroupId()).isEqualTo(expectedResult.getId().getGroupId());
                    assertThat(ug.getRole()).isEqualTo(expectedResult.getRole());
                    assertThat(ug.getVotesRemaining()).isEqualTo(expectedResult.getVotesRemaining());
                }
        );
        verify(userGroupRepository, times(1)).save(any());
    }

    @Test
    void shouldSuccessfullyCreateUserGroupWithGivenRole() {
        //given
        var expectedResult = new UserGroup(new UserGroupKey(1L,1L), Role.COORDINATOR, 1);

        //when
        when(userGroupRepository.save(any(UserGroup.class))).thenAnswer(i -> i.getArguments()[0]);
        var result = userGroupService.createUserGroup(new UserGroupKey(1L, 1L), Role.COORDINATOR, 1);

        //then
        assertThat(result).satisfies(
                ug -> {
                    assertThat(ug.getId().getUserId()).isEqualTo(expectedResult.getId().getUserId());
                    assertThat(ug.getId().getGroupId()).isEqualTo(expectedResult.getId().getGroupId());
                    assertThat(ug.getRole()).isEqualTo(expectedResult.getRole());
                    assertThat(ug.getVotesRemaining()).isEqualTo(expectedResult.getVotesRemaining());
                }
        );
        verify(userGroupRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnTrueIfUserGroupKeyExists() {
        //given
        var key = new UserGroupKey(1L, 1L);

        //when
        when(userGroupRepository.existsById(any(UserGroupKey.class))).thenReturn(true);
        var result = userGroupService.exists(key);

        //then
        verify(userGroupRepository, times(1)).existsById(any());
        Assertions.assertTrue(result);
    }

    @Test
    void shouldGetUserByGroupId() {
        //given
        var userGroup = new UserGroup();

        //when
        when(userGroupRepository.findById(any(UserGroupKey.class))).thenReturn(Optional.of(userGroup));
        var result = userGroupService.getUserGroupById(new UserGroupKey(1L, 1L));

        //then
        verify(userGroupRepository, times(1)).findById(any());
        assertThat(result).isEqualTo(userGroup);
    }

    @Test
    void shoulThrowErrorWhenUserGroupNotFound() {
        //when
        when(userGroupRepository.findById(any(UserGroupKey.class))).thenReturn(Optional.empty());
        var exception = assertThrows(IllegalArgumentException.class,
                () -> userGroupService.getUserGroupById(new UserGroupKey(1L, 1L)));

        //then
        verify(userGroupRepository, times(1)).findById(any());
        assertThat(exception.getMessage()).isEqualTo("User is not a member of the group");
    }

    @Test
    void shouldReturnTrueIfUserIsCoordinator() {
        //given
        var userGroup = new UserGroup(new UserGroupKey(1L, 1L), Role.COORDINATOR, 1);

        //when
        when(userGroupRepository.findById(any(UserGroupKey.class))).thenReturn(Optional.of(userGroup));
        var result = userGroupService.isUserCoordinator(1L, 1L);

        //then
        verify(userGroupRepository, times(1)).findById(any());
        Assertions.assertTrue(result);
    }

    @Test
    void shouldReturnFalseIfUserIsNotCoordinator() {
        //when
        when(userGroupRepository.findById(any(UserGroupKey.class))).thenReturn(Optional.empty());
        var result = userGroupService.isUserCoordinator(1L, 1L);

        //then
        verify(userGroupRepository, times(1)).findById(any());
        Assertions.assertFalse(result);
    }

    @Test
    void shouldDeleteGroupCleanUp() {
        //given
        var userGroups = List.of(new UserGroup(new UserGroupKey(1L, 1L), Role.COORDINATOR, 1));

        //when
        when(userGroupRepository.findAllById_GroupId(anyLong())).thenReturn(userGroups);
        userGroupService.deletionGroupCleanUp(1L);

        //then
        verify(userGroupRepository, times(1)).findAllById_GroupId(anyLong());
        verify(userGroupRepository, times(1)).deleteAll(userGroups);
    }

    @Test
    void shouldThrowExceptionWhenNoGroupsWereFound() {
        //given
        List<UserGroup> userGroups = List.of();

        //when
        when(userGroupRepository.findAllById_GroupId(anyLong())).thenReturn(userGroups);
        var exception = assertThrows(ApiRequestException.class,
                                     () -> userGroupService.deletionGroupCleanUp(1L));
        //then
        assertThat(exception.getMessage()).isEqualTo("No user group entity matching given userId or groupId was found ");
        verify(userGroupRepository, times(1)).findAllById_GroupId(anyLong());
        verify(userGroupRepository, never()).deleteAll(userGroups);
    }

    @Test
    void shouldReturnTrueIfUserInGroup() {
        //when
        when(userGroupRepository.existsById(any(UserGroupKey.class))).thenReturn(true);
        var result = userGroupService.checkIfUserIsInGroup(1L ,1L);

        //then
        Assertions.assertTrue(result);
        verify(userGroupRepository, times(1)).existsById(any());
    }

    @Test
    void shouldThrowExceptionWhenInputInvalid() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> userGroupService.checkIfUserIsInGroup(1L, null));

        //then
        assertThat(exception.getMessage()).isEqualTo("User id and group id must be positive");
        verify(userGroupRepository, never()).existsById(any());
    }

    @Test
    void shouldSuccessfullyDeleteUserFromGroup() {
        //when
        userGroupService.deleteUserFromGroup(1L, 1L);

        //then
        verify(userGroupRepository, times(1)).deleteById(any(UserGroupKey.class));
    }

    @Test
    void shouldGetNumberOfParticipants() {
        //when
        when(userGroupRepository.countAllById_GroupId(anyLong())).thenReturn(1);
        var result = userGroupService.getNumberOfParticipants(1L);

        //then
        verify(userGroupRepository, times(1)).countAllById_GroupId(any());
        assertThat(result).isEqualTo(1);
    }

    @Test
    void shouldGetAllUsersInGroup() {
        //when
        when(appUserProxy.getUsersByIds(anyString(), any())).thenReturn(List.of());
        userGroupService.getAllUsersInGroup( 1L);

        //then
        verify(appUserProxy, times(1)).getUsersByIds(anyString(), any());
    }

    @Test
    void shouldGetAllCoordinatorsInGroup() {
        //when
        when(appUserProxy.getUsersByIds(anyString(), any())).thenReturn(List.of());
        userGroupService.getAllCoordinatorsInGroup( 1L);

        //then
        verify(appUserProxy, times(1)).getUsersByIds(anyString(), any());
    }

    @Test
    void shouldGetAllCoordinatorIds() {
        //when
        when(userGroupRepository.findAllById_GroupIdAndRoleEquals(1L, Role.COORDINATOR)).thenReturn(List.of());
        userGroupService.getAllCoordinatorsIdsInGroup( 1L);

        //then
        verify(userGroupRepository, times(1)).findAllById_GroupIdAndRoleEquals(1L, Role.COORDINATOR);
    }

    @Test
    void shouldReturnErrorWhenGroupIdInvalid() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> userGroupService.getAllUsersInGroup( null));

        //then
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number");
    }

    @Test
    void shouldReturnErrorWhenGroupIdInvalidGetAllCoordinator() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> userGroupService.getAllCoordinatorsInGroup( null));

        //then
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number");
    }

    @Test
    void shouldReturnErrorWhenGroupIdInvalidGetAllCoordinatorIds() {
        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> userGroupService.getAllCoordinatorsIdsInGroup( null));

        //then
        assertThat(exception.getMessage()).isEqualTo("Group id is invalid. Id must be a positive number");
    }


}