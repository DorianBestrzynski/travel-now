package com.zpi.availabilityservice.sharedGroupAvailability;

import com.zpi.availabilityservice.availability.Availability;
import com.zpi.availabilityservice.availability.AvailabilityRepository;
import com.zpi.availabilityservice.dto.AvailabilityConstraintsDto;
import com.zpi.availabilityservice.proxies.TripGroupProxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class SharedGroupAvailabilityServiceTest {

    @MockBean
    TripGroupProxy tripGroupProxy;

    @MockBean
    AvailabilityRepository availabilityRepository;

    @Autowired
    @InjectMocks
    SharedGroupAvailabilityService sharedGroupAvailabilityService;

    @MockBean
    SharedGroupAvailabilityRepository sharedGroupAvailabilityRepository;

    @Captor
    ArgumentCaptor<List<SharedGroupAvailability>> argumentCaptor;

    @Test
    void shouldGenerateSharedGroupAvailabilityForDisjointAvailabilities() {
        //given
        var availabilities = getDisJointAvailabilities();

        //when
        doReturn(new AvailabilityConstraintsDto(3, 1, null)).when(tripGroupProxy).getAvailabilityConstraints(any(), any());
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        doNothing().when(sharedGroupAvailabilityRepository).deleteAllByGroupId(anyLong());
        sharedGroupAvailabilityService.generateSharedGroupAvailability(1L);

        //then
        verify(sharedGroupAvailabilityRepository).saveAll(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().size());
        assertEquals(availabilities.get(1).getDateFrom(), argumentCaptor.getValue().get(0).getDateFrom());
        assertEquals(availabilities.get(1).getDateTo(), argumentCaptor.getValue().get(0).getDateTo());
    }

    @Test
    void shouldGenerateSharedGroupAvailabilityAllSatisfyMinDays() {
        //given
        var availabilities = getAvailabilitiesWithGivenLength(3);

        //when
        doReturn(new AvailabilityConstraintsDto(3, 3, null)).when(tripGroupProxy).getAvailabilityConstraints(any(), any());
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        doNothing().when(sharedGroupAvailabilityRepository).deleteAllByGroupId(anyLong());
        sharedGroupAvailabilityService.generateSharedGroupAvailability(1L);

        //then
        verify(sharedGroupAvailabilityRepository).saveAll(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().size());
        assertEquals(availabilities.get(0).getDateFrom(), argumentCaptor.getValue().get(0).getDateFrom());
        assertEquals(availabilities.get(0).getDateTo(), argumentCaptor.getValue().get(0).getDateTo());
    }

    @Test
    void shouldGenerateAllTheBestSharedGroupAvailability() {
        //given
        var availabilities = getDisjointAvailabilities4Users();

        //when
        doReturn(new AvailabilityConstraintsDto(3, 2, null)).when(tripGroupProxy).getAvailabilityConstraints(any(), any());
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        doNothing().when(sharedGroupAvailabilityRepository).deleteAllByGroupId(anyLong());
        sharedGroupAvailabilityService.generateSharedGroupAvailability(1L);

        //then
        verify(sharedGroupAvailabilityRepository).saveAll(argumentCaptor.capture());
        assertEquals(2, argumentCaptor.getValue().size());
        assertThat(argumentCaptor.getValue()).satisfies(
                actual -> assertThat(actual.get(0).getNumberOfDays()).isEqualTo(actual.get(1).getNumberOfDays())
        );
    }

    @Test
    void shouldNotDuplicateAlreadyDefinedSharedAvailabilityNotCreatedManually() {
        //given
        var availability = new Availability(1L, 1L, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 7));
        var availability2 = new Availability(2L, 1L, LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 10));
        var availability3 = new Availability(3L, 1L, LocalDate.of(2021, 1, 8), LocalDate.of(2021, 1, 12));
        var sharedGroupAvailability = new SharedGroupAvailability(1L, List.of(1L,2L), LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 7), 3, false);

        var availabilities = List.of(availability, availability2, availability3);
        //when
        doReturn(new AvailabilityConstraintsDto(2, 2, 1L)).when(tripGroupProxy).getAvailabilityConstraints(any(), any());
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        doNothing().when(sharedGroupAvailabilityRepository).deleteAllByGroupId(anyLong());
        when(sharedGroupAvailabilityRepository.findById(1L)).thenReturn(Optional.of(sharedGroupAvailability));
        sharedGroupAvailabilityService.generateSharedGroupAvailability(1L);

        //then
        verify(sharedGroupAvailabilityRepository).saveAll(argumentCaptor.capture());
        assertEquals(1, argumentCaptor.getValue().size());
        assertThat(argumentCaptor.getValue()).singleElement().satisfies(
                actual -> {
                    assertThat(actual.getNumberOfDays()).isEqualTo(3);
                    assertThat(actual.getUsersList()).isEqualTo(List.of(2L, 3L));
                    assertThat(actual.getDateFrom()).isEqualTo(LocalDate.of(2021, 1, 8));
                    assertThat(actual.getDateTo()).isEqualTo(LocalDate.of(2021, 1, 10));
                    assertThat(actual.getIsCreatedManually()).isFalse();
                }
        );
    }

    @Test
    void shouldIgnoreWhenSelectedSharedAvailabilityWasAddedManuallyWhenDuplicates() {
        //given
        var availability = new Availability(1L, 1L, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 7));
        var availability2 = new Availability(2L, 1L, LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 10));
        var availability3 = new Availability(3L, 1L, LocalDate.of(2021, 1, 8), LocalDate.of(2021, 1, 12));
        var sharedGroupAvailability = new SharedGroupAvailability(1L, List.of(1L,2L), LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 7), 3, true);

        var availabilities = List.of(availability, availability2, availability3);
        //when
        doReturn(new AvailabilityConstraintsDto(2, 2, 1L)).when(tripGroupProxy).getAvailabilityConstraints(any(), any());
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        doNothing().when(sharedGroupAvailabilityRepository).deleteAllByGroupId(anyLong());
        when(sharedGroupAvailabilityRepository.findById(1L)).thenReturn(Optional.of(sharedGroupAvailability));
        sharedGroupAvailabilityService.generateSharedGroupAvailability(1L);

        //then
        verify(sharedGroupAvailabilityRepository).saveAll(argumentCaptor.capture());
        assertEquals(2, argumentCaptor.getValue().size());
        assertThat(argumentCaptor.getValue()).satisfies(
                actual -> {
                    assertThat(actual.get(0).getNumberOfDays()).isEqualTo(actual.get(1).getNumberOfDays());
                    assertThat(actual.get(0).getUsersList().size()).isEqualTo(actual.get(1).getUsersList().size());
                }
        );
    }

    @Test
    void shouldThrowErrorWhenSelectedSharedAvailabilityNotFound() {
        //given
        var availability = new Availability(1L, 1L, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 7));
        var availabilities = List.of(availability);

        //when
        doReturn(new AvailabilityConstraintsDto(2, 2, 1L)).when(tripGroupProxy).getAvailabilityConstraints(any(), any());
        when(availabilityRepository.findAvailabilitiesByGroupId(anyLong())).thenReturn(availabilities);
        doNothing().when(sharedGroupAvailabilityRepository).deleteAllByGroupId(anyLong());
        when(sharedGroupAvailabilityRepository.findById(1L)).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () ->  sharedGroupAvailabilityService.generateSharedGroupAvailability(1L));

        //then
        verify(sharedGroupAvailabilityRepository, never()).saveAll(any());
        assertThat(exception.getMessage()).isEqualTo("Shared availability not found");
    }

    @Test
    void shouldReturnOnlyAvailabilitiesNotCreatedManually() {
        //given
        var sharedGroupAvailability = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L,6L), LocalDate.now(), LocalDate.now(), 4, false);
        var sharedGroupAvailability2 = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L), LocalDate.now(), LocalDate.now(), 7, true);
        Long groupId = 1L;

        //when
        when(sharedGroupAvailabilityRepository.findAllByGroupId(groupId)).thenReturn(List.of(sharedGroupAvailability, sharedGroupAvailability2));
        var result = sharedGroupAvailabilityService.getGroupSharedAvailabilities(groupId);

        //then
        verify(sharedGroupAvailabilityRepository, times(1)).findAllByGroupId(groupId);

        assertThat(result).singleElement().satisfies(
              actual -> {
                  assertThat(actual.getUsersList().size()).isEqualTo(6);
                  assertThat(actual.getNumberOfDays()).isEqualTo(4);
                  assertThat(actual.getIsCreatedManually()).isFalse();
              }
        );
    }

    private List<Availability> getAvilabilitiesForTest() {
        return List.of(
                new Availability(1L, 1L, LocalDate.of(2022, 6,15), LocalDate.of(2022, 6, 25)),
                new Availability(1L, 1L, LocalDate.of(2022, 7,17), LocalDate.of(2022, 8, 8)),
                new Availability(1L, 1L, LocalDate.of(2022, 9,1), LocalDate.of(2022, 9, 31)),
                new Availability(2L, 1L, LocalDate.of(2022, 7,1), LocalDate.of(2022, 7, 5)),
                new Availability(2L, 1L, LocalDate.of(2022, 7,25), LocalDate.of(2022, 8, 25)),
                new Availability(2L, 1L, LocalDate.of(2022, 9,15), LocalDate.of(2022, 9, 31)),
                new Availability(3L, 1L, LocalDate.of(2022, 6,30), LocalDate.of(2022, 7, 10)),
                new Availability(3L, 1L, LocalDate.of(2022, 7,13), LocalDate.of(2022, 7, 19)),
                new Availability(3L, 1L, LocalDate.of(2022, 7,27), LocalDate.of(2022, 8, 28)),
                new Availability(3L, 1L, LocalDate.of(2022, 9,3), LocalDate.of(2022, 9, 13))
        );
    }

    private List<Availability> getDisJointAvailabilities() {
        return List.of(
                new Availability(1L, 1L, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 3)),
                new Availability(2L, 1L, LocalDate.of(2021, 1, 5), LocalDate.of(2021, 1, 8))
        );
    }

    private List<Availability> getAvailabilitiesWithGivenLength(int minDays) {
        var baseDate = LocalDate.now();
        return List.of(
                new Availability(1L, 1L, baseDate, baseDate.plusDays(minDays)),
                new Availability(2L, 1L, baseDate, baseDate.plusDays(minDays)),
                new Availability(3L, 1L, baseDate, baseDate.plusDays(minDays))
        );
    }

    private List<Availability> getDisjointAvailabilities4Users() {
        return List.of(
                new Availability(1L, 1L, LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 15)),
                new Availability(2L, 1L, LocalDate.of(2022, 1, 1), LocalDate.of(2022, 1, 15)),
                new Availability(3L, 1L, LocalDate.of(2022, 2, 1), LocalDate.of(2022, 2, 15)),
                new Availability(4L, 1L, LocalDate.of(2022, 2, 1), LocalDate.of(2022, 2, 15))
        );
    }

    @Test
    void shouldCorrectlyFilterAvailabilities() {
        //given
        var sharedGroupAvailability = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L,6L), LocalDate.now(), LocalDate.now(), 7, false);
        var sharedGroupAvailability2 = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L), LocalDate.now(), LocalDate.now(), 7, false);
        var sharedGroupAvailability3 = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L), LocalDate.now(), LocalDate.now(), 3, false);
        var sharedGroupAvailability5 = new SharedGroupAvailability(1L, List.of(1L,2L,6L,3L), LocalDate.now(), LocalDate.now(), 8, false);
        var sharedGroupAvailability6 = new SharedGroupAvailability(1L, List.of(1L,2L,6L), LocalDate.now(), LocalDate.now(), 9, false);
        var listOfAvailabilities = List.of(sharedGroupAvailability, sharedGroupAvailability2, sharedGroupAvailability3, sharedGroupAvailability5, sharedGroupAvailability6);
        //when
        var result = sharedGroupAvailabilityService.filterAvailabilities(listOfAvailabilities);
        var expectedResult = List.of(sharedGroupAvailability, sharedGroupAvailability5, sharedGroupAvailability6);
        //then
        assertEquals(expectedResult, result);
    }

}