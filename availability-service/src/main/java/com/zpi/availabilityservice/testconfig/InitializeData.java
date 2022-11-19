package com.zpi.availabilityservice.testconfig;

import com.zpi.availabilityservice.availability.Availability;
import com.zpi.availabilityservice.availability.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeData {
    @Value("${spring.profiles.active:default}")
    private String profile;

    private final AvailabilityRepository availabilityRepository;

    @PostConstruct
    public void addAvailabilities() {
        if (!profile.equals("test"))
            return;

        var availabilities = List.of(
                new Availability(1L, 1L, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 3)),
                new Availability(1L, 1L, LocalDate.of(2021, 5, 5), LocalDate.of(2021, 5, 15)),
                new Availability(1L, 1L, LocalDate.of(2021, 5, 23), LocalDate.of(2021, 5, 28)),
                new Availability(1L, 1L, LocalDate.of(2021, 5, 30), LocalDate.of(2021, 5, 31)),
                new Availability(2L, 1L, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 10)),
                new Availability(2L, 1L, LocalDate.of(2021, 5, 15), LocalDate.of(2021, 5, 22)),
                new Availability(2L, 1L, LocalDate.of(2021, 5, 25), LocalDate.of(2021, 5, 30)),
                new Availability(2L, 5L, LocalDate.of(2021, 5, 1), LocalDate.of(2021, 5, 5)),
                new Availability(2L, 5L, LocalDate.of(2021, 5, 7), LocalDate.of(2021, 5, 10)),
                new Availability(2L, 5L, LocalDate.of(2021, 5, 12), LocalDate.of(2021, 5, 15)),
                new Availability(2L, 5L, LocalDate.of(2021, 5, 17), LocalDate.of(2021, 5, 29)),
                new Availability(4L, 5L, LocalDate.of(2021, 5, 2), LocalDate.of(2021, 6, 7)),
                new Availability(4L, 5L, LocalDate.of(2021, 6, 9), LocalDate.of(2021, 6, 15)),
                new Availability(4L, 5L, LocalDate.of(2021, 6, 17), LocalDate.of(2021, 6, 20)),
                new Availability(4L, 5L, LocalDate.of(2021, 6, 22), LocalDate.of(2021, 6, 30))
        );

        availabilityRepository.saveAll(availabilities);
    }
}
