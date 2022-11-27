package com.zpi.dayplanservice.attractions;

import com.zpi.dayplanservice.attraction.Attraction;
import com.zpi.dayplanservice.attraction.AttractionRepository;
import com.zpi.dayplanservice.attraction.AttractionService;
import com.zpi.dayplanservice.day_plan.DayPlan;
import com.zpi.dayplanservice.day_plan.DayPlanRepository;
import com.zpi.dayplanservice.day_plan.DayPlanService;
import com.zpi.dayplanservice.dto.AccommodationInfoDto;
import com.zpi.dayplanservice.dto.AttractionCandidateDto;
import com.zpi.dayplanservice.proxies.TripGroupProxy;
import com.zpi.dayplanservice.security.CustomUsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AttractionServiceTests {

    @MockBean
    AttractionRepository attractionRepository;

    @MockBean
    DayPlanRepository dayPlanRepository;

    @MockBean
    TripGroupProxy tripGroupProxy;

    @Autowired
    @InjectMocks
    AttractionService attractionService;

    @MockBean
    DayPlanService dayPlanService;


    void mockAuthorizePartOfTheGroupAspect() {
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserPartOfTheGroup(anyString(), any(), anyLong());
    }

    void mockAuthorizeCoordinatorAspect() {
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(dayPlanRepository.findById(any())).thenReturn(Optional.of(new DayPlan(1L, LocalDate.now(), "Test")));
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserCoordinator(anyString(), any(), anyLong());
    }

    List<Attraction> getSampleAttractions() {
        return List.of(
                new Attraction(1L, 51.1071154, 17.0263899, "National Forum of Music"),
                new Attraction(2L, 51.1412218, 16.9443929, "Wrocław Stadium"),
                new Attraction(3L, 51.1103736, 17.0310308, "Wrocław Market Square"),
                new Attraction(4L, 51.1101287, 17.0443431, "Panorama of the Battle of Racławice"),
                new Attraction(5L, 51.1077469, 17.0625052, "Wrocław University of Science and Technology"),
                new Attraction(6L, 51.070149, 17.057733, "Go Kart Racing Center Wroclaw")
        );
    }

    @Test
    void shouldDeleteAttractionFromDayPlanAndRepository() {
        //given
        mockAuthorizeCoordinatorAspect();
        var dayPlan = new DayPlan(1L, LocalDate.now(), "First plan");
        var attraction = new Attraction();
        attraction.setAttractionId(1L);
        attraction.addDays(List.of(dayPlan));
        dayPlan.addAttraction(attraction);

        //when
        when(dayPlanService.getDayPlanById(anyLong())).thenReturn(dayPlan);
        var result = attractionService.deleteAttraction(1L, 1L);

        //then
        verify(attractionRepository).delete(attraction);
        assertEquals(dayPlan.getDayAttractions().size(), 0);
        assertEquals(result.getDays().size(), 0);
    }

    @Test
    void shouldNotDeleteAttractionFromRepository() {
        //given
        mockAuthorizeCoordinatorAspect();
        var firstDay = new DayPlan(1L, LocalDate.now(), "First plan");
        var secondPlan = new DayPlan(2L, LocalDate.now(), "Second plan");
        var attraction = new Attraction();
        attraction.setAttractionId(1L);
        attraction.addDays(List.of(firstDay, secondPlan));
        firstDay.addAttraction(attraction);
        secondPlan.addAttraction(attraction);

        //when
        when(dayPlanService.getDayPlanById(anyLong())).thenReturn(firstDay);
        var result = attractionService.deleteAttraction(1L, 1L);

        //then
        verify(attractionRepository, never()).delete(attraction);
        assertEquals(0, firstDay.getDayAttractions().size());
        assertEquals(1, secondPlan.getDayAttractions().size());
        assertEquals(1, result.getDays().size());
    }

    @Test
    void shouldAddNewAttractionToAllDayPlans() {
        //given
        mockAuthorizeCoordinatorAspect();
        var firstDay = new DayPlan(1L, LocalDate.now(), "First plan");
        var secondPlan = new DayPlan(2L, LocalDate.now(), "Second plan");
        var attractionCandidateDto = new AttractionCandidateDto();

        //when
        when(dayPlanService.getDayPlanById(anyList(), anyLong())).thenReturn(List.of(firstDay, secondPlan));
        when(attractionRepository.save(any(Attraction.class))).thenAnswer(i -> i.getArguments()[0]);
        var result = attractionService.addAttraction(List.of(1L, 2L), attractionCandidateDto);

        //then
        verify(attractionRepository).save(any(Attraction.class));
        assertEquals(2, result.getDays().size());
        assertEquals(1, firstDay.getDayAttractions().size());
        assertEquals(1, firstDay.getDayAttractions().size());
    }

    @Test
    void shouldReturnOptimalDayPlanFromGivenPoint() {
        //given
        var attractions = getSampleAttractions();
        var dayPlan = new DayPlan(1L, LocalDate.now(), "test");
        dayPlan.setDayAttractions(new HashSet<>(attractions));
        dayPlan.setDayPlanStartingPointId(5L);

        //when
        when(dayPlanService.getDayPlanById(anyLong())).thenReturn(dayPlan);
        var result = attractionService.findOptimalDayPlan(1L);

        //then
        assertArrayEquals(result.stream()
                           .mapToLong(dto -> dto.getAttraction().getAttractionId())
                           .toArray(), new long[]{5L, 4L, 3L, 1L, 6L, 2L});
    }

    @Test
    void shouldReturnOptimalDayPlanFromAccommodation() {
        //given
        var attractions = getSampleAttractions();
        var dayPlan = new DayPlan(1L, LocalDate.now(), "test");
        var accommodation = new AccommodationInfoDto("Ovo Wrocław", null,51.108177, 17.043310, 1L);
        dayPlan.setDayAttractions(new HashSet<>(attractions));

        //when
        when(dayPlanService.getDayPlanById(anyLong())).thenReturn(dayPlan);
        when(tripGroupProxy.getGroupAccommodation(anyString(), anyLong())).thenReturn(accommodation);
        var result = attractionService.findOptimalDayPlan(1L);

        //then
        assertArrayEquals(result.stream()
                                .mapToLong(dto -> dto.getAttraction().getAttractionId())
                                .toArray(), new long[]{4L, 3L, 1L, 5L, 6L, 2L});
    }

    @Test
    void shouldReturnOptimalDayPlanFromAllPossible() {
        //given
        var attractions = getSampleAttractions();
        var dayPlan = new DayPlan(1L, LocalDate.now(), "test");
        dayPlan.setDayAttractions(new HashSet<>(attractions));

        //when
        when(dayPlanService.getDayPlanById(anyLong())).thenReturn(dayPlan);
        when(tripGroupProxy.getGroupAccommodation(anyString(), anyLong())).thenReturn(null);
        var result = attractionService.findOptimalDayPlan(1L);

        //then
        assertArrayEquals(result.stream()
                                .mapToLong(dto -> dto.getAttraction().getAttractionId())
                                .toArray(), new long[]{2L, 3L, 4L, 5L, 1L, 6L});
    }

    @Test
    void shouldReturnCandidatesForAttraction() {
        //when
        var result = attractionService.findCandidates("camp nou");

        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyListInsteadOfError() {
        //given
        var wrongSearchQuery = "dadasdsad";
        //when
        var result = attractionService.findCandidates(wrongSearchQuery);

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionsOnInvalidInput() {
        //given
        mockAuthorizeCoordinatorAspect();
        mockAuthorizePartOfTheGroupAspect();
        when(attractionRepository.existsById(any())).thenReturn(Boolean.FALSE);

        //then
        assertThrows(IllegalArgumentException.class, () -> attractionService.deleteAttraction(null, null));
        assertThrows(IllegalArgumentException.class, () -> attractionService.deleteAttraction(-1L, -2L));
        assertThrows(IllegalArgumentException.class, () -> attractionService.addAttraction(new ArrayList<Long>(), null));
        assertThrows(IllegalArgumentException.class, () -> attractionService.addAttraction(List.of(1L), null));
        assertThrows(IllegalArgumentException.class, () -> attractionService.editAttraction(null));
        assertThrows(IllegalArgumentException.class, () -> attractionService.editAttraction(new Attraction()));
    }
}
