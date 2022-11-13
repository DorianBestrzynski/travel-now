package com.zpi.dayplanservice.day_plan;

import com.zpi.dayplanservice.attraction.Attraction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class DayPlanTests {

    DayPlan prepareDayPlan() {
        var dayPlan = new DayPlan(1L, LocalDate.now(), "Test day plan");
        var attraction = new Attraction();
        attraction.setAttractionId(1L);
        dayPlan.addAttraction(attraction);
        return dayPlan;
    }

    @Test
    void shouldDeleteAttractionFromDayPlan() {
        //given
        var dayPlan = prepareDayPlan();

        //when
        dayPlan.deleteAttraction(1L);

        //then
        assertTrue(dayPlan.getDayAttractions().isEmpty());
    }

    @Test
    void shouldThrowException() {
        //given
        var dayPlan = prepareDayPlan();

        //then
        assertThrows(IllegalArgumentException.class, () -> dayPlan.deleteAttraction(2L));
    }
}
