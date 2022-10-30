package com.zpi.availabilityservice.sharedGroupAvailability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SharedGroupAvailabilityServiceTest {

    @Autowired
    SharedGroupAvailabilityService sharedGroupAvailabilityService;

    @Test
    void shouldCorrectlyFilterAvailabilities() {
        //given
        var sharedGroupAvailability = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L,6L), LocalDate.now(), LocalDate.now(), 7);
        var sharedGroupAvailability2 = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L), LocalDate.now(), LocalDate.now(), 7);
        var sharedGroupAvailability3 = new SharedGroupAvailability(1L, List.of(1L,2L,3L,4L,5L), LocalDate.now(), LocalDate.now(), 3);
        var sharedGroupAvailability5 = new SharedGroupAvailability(1L, List.of(1L,2L,6L,3L), LocalDate.now(), LocalDate.now(), 8);
        var sharedGroupAvailability6 = new SharedGroupAvailability(1L, List.of(1L,2L,6L), LocalDate.now(), LocalDate.now(), 9);
        var listOfAvailabilities = List.of(sharedGroupAvailability, sharedGroupAvailability2, sharedGroupAvailability3, sharedGroupAvailability5, sharedGroupAvailability6);
        //when
        var result = sharedGroupAvailabilityService.filterAvailabilities(listOfAvailabilities);
        var expectedResult = List.of(sharedGroupAvailability, sharedGroupAvailability5, sharedGroupAvailability6);
        //then
        assertEquals(expectedResult, result);
    }
}