package com.zpi.availabilityservice.availability;

import com.zpi.availabilityservice.dto.AvailabilityConstraintsDto;
import com.zpi.availabilityservice.dto.AvailabilityDto;
import com.zpi.availabilityservice.dto.UserDto;
import com.zpi.availabilityservice.events.publisher.GenerationAvailabilityPublisher;
import com.zpi.availabilityservice.exceptions.ApiPermissionException;
import com.zpi.availabilityservice.exceptions.IllegalDatesException;
import com.zpi.availabilityservice.proxies.AppUserProxy;
import com.zpi.availabilityservice.proxies.TripGroupProxy;
import com.zpi.availabilityservice.security.CustomUsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AvailabilityServiceTests {

    @MockBean
    AvailabilityRepository availabilityRepository;

    @MockBean
    TripGroupProxy tripGroupProxy;

    @SpyBean
    GenerationAvailabilityPublisher generationAvailabilityPublisher;

    @Autowired
    @InjectMocks
    AvailabilityService availabilityService;

    @MockBean
    AppUserProxy appUserProxy;

    private final Random r = new Random();

    void mockAuthorizePartOfTheGroupAspect() {
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserPartOfTheGroup(anyString(), any(), anyLong());
    }

    void mockAuthorizeCoordinatorAspect() {
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserCoordinator(anyString(), any(), anyLong());
    }

    @BeforeEach
    void mockGetAvailabilityConstraints() {
        doReturn(new AvailabilityConstraintsDto(3, 3)).when(tripGroupProxy).getAvailabilityConstraints(any());
    }

    @Test
    void shouldAddNewAvailability() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var availabilityDto = new AvailabilityDto(1L, 1L, LocalDate.now().minusDays(r.nextInt(10)), LocalDate.now());
        var expected = new Availability(availabilityDto.userId(), availabilityDto.groupId(), availabilityDto.dateFrom(), availabilityDto.dateTo());

        //when
        doNothing().when(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        when(availabilityRepository.findOverlapping(availabilityDto.userId(),
                                                    availabilityDto.groupId(),
                                                    availabilityDto.dateFrom(),
                                                    availabilityDto.dateTo())).thenReturn(new ArrayList<Availability>());
        when(availabilityRepository.save(any())).thenReturn(expected);
        var result = availabilityService.addNewAvailability(availabilityDto);

        //then
        verify(availabilityRepository).save(any(Availability.class));
        verify(availabilityRepository).deleteAll(new ArrayList<Availability>());
        verify(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                    assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                    assertThat(actual.getDateFrom()).isEqualTo(expected.getDateFrom());
                    assertThat(actual.getDateTo()).isEqualTo(expected.getDateTo());

                }
        );
    }

    @Test
    void shouldMergeAvailabilityAndAddMergedCase1() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var availabilityDto = new AvailabilityDto(1L, 1L, LocalDate.now().minusDays(5), LocalDate.now());
        var existingAvailability = new Availability(availabilityDto.userId(), availabilityDto.groupId(), availabilityDto.dateTo().minusDays(3), availabilityDto.dateTo().plusDays(3));
        var expected = new Availability(availabilityDto.userId(), availabilityDto.groupId(), availabilityDto.dateFrom(), existingAvailability.getDateTo());

        //when
        doNothing().when(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        when(availabilityRepository.findOverlapping(availabilityDto.userId(),
                                                    availabilityDto.groupId(),
                                                    availabilityDto.dateFrom(),
                                                    availabilityDto.dateTo())).thenReturn(List.of(existingAvailability));
        var result = availabilityService.addNewAvailability(availabilityDto);

        //then
        verify(availabilityRepository).save(any(Availability.class));
        verify(availabilityRepository).deleteAll(List.of(existingAvailability));
        verify(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                    assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                    assertThat(actual.getDateFrom()).isEqualTo(expected.getDateFrom());
                    assertThat(actual.getDateTo()).isEqualTo(expected.getDateTo());
                }
        );
    }

    @Test
    void shouldMergeAvailabilityAndAddMergedCase2() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var availabilityDto = new AvailabilityDto(1L, 1L, LocalDate.now().minusDays(5), LocalDate.now());
        var existingAvailability = new Availability(availabilityDto.userId(), availabilityDto.groupId(), availabilityDto.dateFrom().minusDays(3), availabilityDto.dateFrom().plusDays(3));
        var expected = new Availability(availabilityDto.userId(), availabilityDto.groupId(), existingAvailability.getDateFrom(), availabilityDto.dateTo());

        //when
        doNothing().when(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        when(availabilityRepository.findOverlapping(availabilityDto.userId(),
                                                    availabilityDto.groupId(),
                                                    availabilityDto.dateFrom(),
                                                    availabilityDto.dateTo())).thenReturn(List.of(existingAvailability));
        var result = availabilityService.addNewAvailability(availabilityDto);

        //then
        verify(availabilityRepository).save(any(Availability.class));
        verify(availabilityRepository).deleteAll(List.of(existingAvailability));
        verify(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                    assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                    assertThat(actual.getDateFrom()).isEqualTo(expected.getDateFrom());
                    assertThat(actual.getDateTo()).isEqualTo(expected.getDateTo());
                }
        );
    }

    @Test
    void shouldMergeAvailabilityAndAddMergedCase3() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var availabilityDto = new AvailabilityDto(1L, 1L, LocalDate.now().minusDays(5), LocalDate.now());
        var existingAvailability1 = new Availability(availabilityDto.userId(), availabilityDto.groupId(), availabilityDto.dateFrom().minusDays(3), availabilityDto.dateFrom().plusDays(3));
        var existingAvailability2 = new Availability(availabilityDto.userId(), availabilityDto.groupId(), availabilityDto.dateTo().minusDays(3), availabilityDto.dateTo().plusDays(3));
        var expected = new Availability(availabilityDto.userId(), availabilityDto.groupId(), existingAvailability1.getDateFrom(), existingAvailability2.getDateTo());

        //when
        doNothing().when(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        when(availabilityRepository.findOverlapping(availabilityDto.userId(),
                                                    availabilityDto.groupId(),
                                                    availabilityDto.dateFrom(),
                                                    availabilityDto.dateTo())).thenReturn(List.of(existingAvailability1, existingAvailability2));
        var result = availabilityService.addNewAvailability(availabilityDto);

        //then
        verify(availabilityRepository).save(any(Availability.class));
        verify(availabilityRepository).deleteAll(List.of(existingAvailability1, existingAvailability2));
        verify(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                    assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                    assertThat(actual.getDateFrom()).isEqualTo(expected.getDateFrom());
                    assertThat(actual.getDateTo()).isEqualTo(expected.getDateTo());
                }
        );
    }

    @Test
    void shouldReturnUserAvailabilities() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(availabilityRepository.findAvailabilitiesByUserIdAndGroupId(anyLong(), anyLong())).thenReturn(new ArrayList<Availability>());
        availabilityService.getUserAvailabilitiesInTripGroup(1L, 1L);

        //then
        verify(availabilityRepository).findAvailabilitiesByUserIdAndGroupId(anyLong(), anyLong());
    }

    @Test
    void shouldReturnAvailabilities() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(new ArrayList<Availability>());
        availabilityService.getAvailabilitiesInTripGroup(1L);

        //then
        verify(availabilityRepository).findAvailabilitiesByGroupId(anyLong());
    }

    @Test
    void shouldReturnAvailabilitiesWithUserData() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var availabilities = List.of(
                new Availability(1L, 1L, LocalDate.now().minusDays(1), LocalDate.now()),
                new Availability(2L, 1L, LocalDate.now().minusDays(2), LocalDate.now()),
                new Availability(2L, 1L, LocalDate.now().minusDays(7), LocalDate.now().minusDays(3))
        );

        var users = List.of(
                new UserDto(1L, "first", "first", "first"),
                new UserDto(2L, "second", "second", "second")
        );

        //when
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        when(appUserProxy.getUsersDtos(any())).thenReturn(users);
        var result = availabilityService.getAvailabilitiesInTripGroupWithUserData(1L);

        //then
        assertEquals(new HashSet<>(users), new HashSet<>(result.keySet()));
        assertEquals(result.get(users.get(0)).size(), 1);
        assertEquals(result.get(users.get(1)).size(), 2);
    }

    @Test
    void shouldDeleteAvailability() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        doNothing().when(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        when(availabilityRepository.findById(anyLong())).thenReturn(Optional.of(new Availability(1L, 1L, LocalDate.now(), LocalDate.now().plusDays(1))));
        availabilityService.deleteAvailability(1L, 1L);

        //then
        verify(availabilityRepository).deleteById(anyLong());
        verify(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
    }

    @ParameterizedTest
    @MethodSource("paramsForChangeAvailability")
    void shouldChangeAvailability(LocalDate from, LocalDate to) {
        //given
        mockAuthorizePartOfTheGroupAspect();

        var toChange = new Availability(1L, 1L, LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));
        Availability expected;
        if(from != null && to != null) {
            expected = new Availability(1L, 1L, from, to);
        } else if(from != null) {
            expected = new Availability(1L, 1L, from, toChange.getDateTo());
        } else {
            expected = new Availability(1L, 1L, toChange.getDateFrom(), to);
        }

        //when
        doNothing().when(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        when(availabilityRepository.findById(anyLong())).thenReturn(Optional.of(toChange));
        var result = availabilityService.changeAvailability(1L, from, to);

        //then
        verify(generationAvailabilityPublisher).publishAvailabilityGenerationEvent(anyLong());
        if(from != null && to != null) {
            assertThat(result).satisfies(
                    actual -> {
                        assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                        assertThat(actual.getDateFrom()).isEqualTo(from);
                        assertThat(actual.getDateTo()).isEqualTo(to);
                    }
            );
        } else if(from != null) {
            assertThat(result).satisfies(
                    actual -> {
                        assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                        assertThat(actual.getDateFrom()).isEqualTo(from);
                        assertThat(actual.getDateTo()).isEqualTo(toChange.getDateTo());
                    }
            );
        } else {
            assertThat(result).satisfies(
                    actual -> {
                        assertThat(actual.getGroupId()).isEqualTo(expected.getGroupId());
                        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
                        assertThat(actual.getDateFrom()).isEqualTo(toChange.getDateFrom());
                        assertThat(actual.getDateTo()).isEqualTo(to);
                    }
            );
        }
    }

    private static Stream<Arguments> paramsForChangeAvailability() {
        return Stream.of(
                Arguments.of(LocalDate.now(), LocalDate.now().plusDays(1)),
                Arguments.of(null, LocalDate.now().plusDays(1)),
                Arguments.of(LocalDate.now(), null)
        );
    }

    @Test
    void shouldThrowExceptionsOnInvalidInput() {
        mockAuthorizeCoordinatorAspect();
        mockAuthorizePartOfTheGroupAspect();

        when(availabilityRepository.findById(anyLong())).thenReturn(Optional.of(new Availability(2L, 1L, LocalDate.now(), LocalDate.now().plusDays(1))));

        assertThrows(IllegalDatesException.class, () -> availabilityService.addNewAvailability(new AvailabilityDto(1L, 1L, LocalDate.now(), LocalDate.now().minusDays(1))));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.getUserAvailabilitiesInTripGroup(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.getUserAvailabilitiesInTripGroup(1L, null));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.getUserAvailabilitiesInTripGroup(-1L, 1L));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.getUserAvailabilitiesInTripGroup(1L, -1L));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.getAvailabilitiesInTripGroupWithUserData(null));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.getAvailabilitiesInTripGroupWithUserData(-1L));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.deleteAvailability(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> availabilityService.deleteAvailability(-1L, 1L));
        assertThrows(ApiPermissionException.class, () -> availabilityService.deleteAvailability(10L, 1L));
    }
}
