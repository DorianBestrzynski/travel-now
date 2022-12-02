package com.zpi.transportservice.flight;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.zpi.transportservice.transport.AirTransport;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Flight {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "flight_sequence"
    )
    @SequenceGenerator(
            name = "flight_sequence",
            sequenceName = "flight_sequence", allocationSize = 10)
    @Column(name = "flight_id",unique = true, nullable = false)
    private Long flightId;

    @Column(name = "flight_number", nullable = false, length = 8)
    private String flightNumber;

    @Column(name = "departure_airport", nullable = false, length = 3)
    private String departureAirport;

    @Column(name = "arrival_airport", nullable = false, length = 3)
    private String arrivalAirport;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "flight_duration")
    @Setter
    private Duration flightDuration;

    @Setter
    @Column(name = "travel_to_airport_duration")
    private Duration travelToAirportDuration = Duration.ZERO;

    @Setter
    @Column(name = "travel_to_accommodation_duration")
    private Duration travelToAccommodationDuration = Duration.ZERO;

    @ManyToOne
    @JoinColumn(name = "transport_id", nullable = false)
    @JsonBackReference
    @Setter
    private AirTransport transport;

    public Flight(String flightNumber, String departureAirport, String arrivalAirport, LocalDateTime departureTime, LocalDateTime arrivalTime, Duration duration) {
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightDuration = duration;
    }

    public Flight(Long flightId, String flightNumber, String departureAirport, String arrivalAirport, LocalDateTime departureTime, LocalDateTime arrivalTime, Duration duration) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightDuration = duration;
    }
}
