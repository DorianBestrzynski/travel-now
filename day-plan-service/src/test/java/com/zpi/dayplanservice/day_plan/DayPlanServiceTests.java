package com.zpi.dayplanservice.day_plan;

import com.zpi.dayplanservice.dto.DayPlanDto;
import com.zpi.dayplanservice.exception.IllegalDateException;
import com.zpi.dayplanservice.mapstruct.MapStructMapper;
import com.zpi.dayplanservice.proxies.TripGroupProxy;
import com.zpi.dayplanservice.security.CustomUsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.zpi.dayplanservice.exception.ExceptionInfo.INVALID_DAY_PLAN_ID;
import static com.zpi.dayplanservice.exception.ExceptionInfo.TAKEN_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class DayPlanServiceTests {

    @MockBean
    DayPlanRepository dayPlanRepository;

    @MockBean
    TripGroupProxy tripGroupProxy;

    @Autowired
    @InjectMocks
    DayPlanService dayPlanService;

    @MockBean
    MapStructMapper mapStructMapper;

    void mockAuthorizePartOfTheGroupAspect() {
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserPartOfTheGroup(anyString(), any(), anyLong());
    }

    void mockAuthorizeCoordinatorAspect() {
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(dayPlanRepository.findById(any())).thenReturn(Optional.of(new DayPlan(1L, LocalDate.now(), "test")));
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserCoordinator(anyString(), any(), anyLong());
    }

    @Test
    void shouldReturnDayPlansForGroup() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(dayPlanRepository.findAllByGroupId(anyLong())).thenReturn(List.of(new DayPlan()));
        dayPlanService.getAllDayPlansForGroup(1L);

        //then
        verify(dayPlanRepository).findAllByGroupId(anyLong());
    }

    @Test
    void shouldCreateNewDayPlan() {
        //given
        mockAuthorizeCoordinatorAspect();
        var dayPlanDto = new DayPlanDto(1L, LocalDate.now(), "test");
        var dayPlan = new DayPlan(dayPlanDto.groupId(), dayPlanDto.date(), dayPlanDto.name());

        //when
        when(dayPlanRepository.findDayPlanByGroupIdAndDate(anyLong(), any())).thenReturn(null);
        when(dayPlanRepository.save(any())).thenReturn(dayPlan);
        var result = dayPlanService.createDayPlan(dayPlanDto);

        //then
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getName()).isEqualTo(dayPlan.getName());
                    assertThat(actual.getGroupId()).isEqualTo(dayPlan.getGroupId());
                    assertThat(actual.getDate()).isEqualTo(dayPlan.getDate());
                }
        );
        verify(dayPlanRepository).findDayPlanByGroupIdAndDate(anyLong(), any(LocalDate.class));
        verify(dayPlanRepository).save(any(DayPlan.class));
    }

    @Test
    void shouldNotCreateNewDayPlan() {
        //given
        mockAuthorizeCoordinatorAspect();
        var dayPlanDto = new DayPlanDto(1L, LocalDate.now(), "test");
        var dayPlan = new DayPlan(dayPlanDto.groupId(), dayPlanDto.date(), dayPlanDto.name());

        //when
        when(dayPlanRepository.findDayPlanByGroupIdAndDate(anyLong(), any())).thenReturn(dayPlan);

        //then
        var exception = assertThrows(IllegalDateException.class, () -> dayPlanService.createDayPlan(dayPlanDto));
        assertTrue(exception.getMessage().contains(TAKEN_DATE));
        verify(dayPlanRepository).findDayPlanByGroupIdAndDate(anyLong(), any(LocalDate.class));
        verify(dayPlanRepository, never()).save(any(DayPlan.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L})
    @NullSource
    void shouldNotDeleteDayPlan(Long dayPlanId) {
        //given
        mockAuthorizeCoordinatorAspect();

        //then
        var exception = assertThrows(IllegalArgumentException.class, () -> dayPlanService.deleteDayPlan(dayPlanId));
        assertTrue(exception.getMessage().contains(INVALID_DAY_PLAN_ID));
        verify(dayPlanRepository, never()).deleteById(anyLong());
    }


    @Test
    void shouldEditDayPlan() {
        //given
        mockAuthorizeCoordinatorAspect();

        var dayPlanDto = new DayPlanDto(1L, LocalDate.now(), "after");
        var dayPlanToUpdate = new DayPlan(1L, LocalDate.now().minusDays(1L), "before");

        //when
        when(dayPlanRepository.findById(anyLong())).thenReturn(Optional.of(dayPlanToUpdate));
        when(dayPlanRepository.save(any(DayPlan.class))).thenReturn(dayPlanToUpdate);
        dayPlanService.editDayPlan(1L, dayPlanDto);

        //then
        verify(mapStructMapper).adaptDayPlanDto(any());
        verify(mapStructMapper).updateFromDayPlanDtoToDayPlan(any(), any());
        verify(dayPlanRepository).save(dayPlanToUpdate);
    }

    @Test
    void shouldReturnDayPlans() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var dayPlans = List.of(
                new DayPlan(1L, LocalDate.now(), "test"),
                new DayPlan(2L, LocalDate.now(), "test"),
                new DayPlan(1L, LocalDate.now(), "test"),
                new DayPlan(3L, LocalDate.now(), "test"),
                new DayPlan(5L, LocalDate.now(), "test")
        );

        var idList = List.of(1L, 2L, 3L, 5L);
        var dayPlan = new DayPlan(1L, LocalDate.now(), "test");

        //when
        when(dayPlanRepository.findById(anyLong())).thenReturn(Optional.of(dayPlan));
        when(dayPlanRepository.findAllById(anyList())).thenReturn(dayPlans);
        var result = dayPlanService.getDayPlanById(idList, 1L);

        //then
        assertThat(result).isEqualTo(dayPlans);
        verify(tripGroupProxy, times(4)).isUserPartOfTheGroup(anyString(), anyLong(), anyLong());
        verify(dayPlanRepository).findAllById(any());
    }

    @Test
    void shouldThrowExceptionsOnInvalidInput() {
        //given
        mockAuthorizeCoordinatorAspect();
        mockAuthorizePartOfTheGroupAspect();
        when(dayPlanRepository.findDayPlanByGroupIdAndDate(any(), any())).thenReturn(new DayPlan());

        //then
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.getAllDayPlansForGroup(null));
        assertThrows(IllegalDateException.class, () -> dayPlanService.createDayPlan(new DayPlanDto(null, null, null)));
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.deleteDayPlan(null));
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.deleteDayPlan(-1L));
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.editDayPlan(-1L, new DayPlanDto(null, null, null)));
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.getDayPlanById(List.of(1L), null));
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.getDayPlanById(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> dayPlanService.getDayPlanById(null));

    }

}
