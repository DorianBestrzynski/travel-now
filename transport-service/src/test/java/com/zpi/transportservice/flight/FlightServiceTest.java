package com.zpi.transportservice.flight;

import com.zpi.transportservice.transport.AirTransport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class FlightServiceTest {

    @Autowired
    FlightService flightService;

    @Test
    void shouldSetFlights() {
        //given
        var flights = List.of(new Flight());
        var airTransport = new AirTransport();

        //when
        flightService.setFlights(flights, airTransport);

        //then
        assertThat(flights).singleElement().satisfies(
                flight -> assertThat(flight.getTransport()).isEqualTo(airTransport)
        );
    }
}