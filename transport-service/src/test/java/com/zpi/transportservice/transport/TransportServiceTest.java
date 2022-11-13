package com.zpi.transportservice.transport;

import com.zpi.transportservice.accommodation_transport.AccommodationTransport;
import com.zpi.transportservice.accommodation_transport.AccommodationTransportId;
import com.zpi.transportservice.accommodation_transport.AccommodationTransportService;
import com.zpi.transportservice.adapter.GeoLocationAdapter;
import com.zpi.transportservice.adapter.LufthansaAdapter;
import com.zpi.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.dto.TripDataDto;
import com.zpi.transportservice.dto.UserTransportDto;
import com.zpi.transportservice.flight.Flight;
import com.zpi.transportservice.flight.FlightService;
import com.zpi.transportservice.proxy.AccommodationProxy;
import com.zpi.transportservice.proxy.TripGroupProxy;
import com.zpi.transportservice.security.CustomUsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class TransportServiceTest {
    @MockBean
    TransportRepository transportRepository;

    @MockBean
    AccommodationTransportService accommodationTransportService;

    @MockBean
    TripGroupProxy tripGroupProxy;

    @MockBean
    AccommodationProxy accommodationProxy;

    @MockBean
    LufthansaAdapter lufthansaAdapter;

    @Autowired
    FlightService flightService;

    @MockBean
    GeoLocationAdapter geoLocationAdapter;

    @Autowired
    @InjectMocks
    TransportService transportService;

    void mockAuthorizeCreatorOrCoordinatorAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(accommodationProxy.getAccommodationInfo(anyString(), anyLong())).thenReturn(new AccommodationInfoDto());
        doReturn(Boolean.TRUE).when(tripGroupProxy).isUserCoordinator(anyString(), anyLong(), anyLong());
    }

    @Test
    void shouldGetTransportForAccommodationWhichAlreadyIsGenerated() {
        //given
        var flights = List.of(new Flight(1L, "LH123", "WRO", "PMI",
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(13,33)),
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(15,33)),
                Duration.ofHours(2)));

        var accommodationTransport = List.of(new AccommodationTransport(new AccommodationTransportId(1L, 1L)),
                new AccommodationTransport(new AccommodationTransportId(2L, 2L)),
                new AccommodationTransport(new AccommodationTransportId(3L, 3L)));
        var accommodationInfo = new AccommodationInfoDto("Mallorcas", "Mallorda", 11.22, 22.22, 1L, 1L);
        var tripInfo = new TripDataDto("Raclawica", LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22), -20.22, 11.11);
        var userTransport = new UserTransport(3L, Duration.ofHours(9),
                        BigDecimal.ONE,
                        "Wroclaw",
                        "Mallorca",
                        LocalDate.of(2022, 11, 15),
                        LocalDate.of(2022, 11, 22),
                        "link",
                        "train",
                        "Desc",
                        LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(10, 0)));

        var airTransport = new AirTransport(1L,
                        Duration.ofHours(2),
                        BigDecimal.TEN,
                        "Wroclaw",
                        "Mallorca",
                        LocalDate.of(2022, 11, 15),
                        LocalDate.of(2022, 11, 22),
                        "link",
                        flights);

        var carTransport = new CarTransport(2L,
                Duration.ofHours(7),
                123L,
                BigDecimal.TEN,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link");

        //when
        when(accommodationTransportService.findAccommodationTransport(anyLong())).thenReturn(accommodationTransport);
        when(accommodationProxy.getAccommodationInfo(anyString(), anyLong())).thenReturn(accommodationInfo);
        when(tripGroupProxy.getTripData(anyString(), anyLong())).thenReturn(tripInfo);
        when(transportRepository.findUserTransport(anyList())).thenReturn(List.of(userTransport));
        when(transportRepository.findAirTransport(anyList())).thenReturn(List.of(airTransport));
        when(transportRepository.findCarTransport(anyList())).thenReturn(List.of(carTransport));
        var actualResult = transportService.getTransportForAccommodation(1L);

        //then
        var expectedResult = List.of(airTransport, carTransport, userTransport);
        assertThat(actualResult).hasSameElementsAs(expectedResult);
        verify(accommodationTransportService, times(1)).findAccommodationTransport(anyLong());
        verify(accommodationTransportService, never()).createAccommodationTransport(anyLong(), anyLong());
        verify(accommodationProxy, times(1)).getAccommodationInfo(anyString(), anyLong());
        verify(tripGroupProxy, times(1)).getTripData(anyString(), anyLong());
        verify(transportRepository, times(1)).findUserTransport(anyList());
        verify(transportRepository, times(1)).findAirTransport(anyList());
        verify(transportRepository, times(1)).findCarTransport(anyList());
        verify(transportRepository, never()).findMatchingAirTransport(anyString(), anyString(), any());
        verify(transportRepository, never()).findMatchingCarTransport(anyString(), anyString(), any());
        verify(lufthansaAdapter, never()).generateTransportAir(any(), any());
    }

    @Test
    void shouldGetTransportForAccommodationWhenMatchingTransportIsPresent() {
        //given
        var flights = List.of(new Flight(1L, "LH123", "WRO", "PMI",
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(13,33)),
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(15,33)),
                Duration.ofHours(2)));

        var accommodationTransport = List.of(new AccommodationTransport(new AccommodationTransportId(1L, 1L)),
                new AccommodationTransport(new AccommodationTransportId(2L, 2L)),
                new AccommodationTransport(new AccommodationTransportId(3L, 3L)));
        var accommodationInfo = new AccommodationInfoDto("Mallorcas", "Mallorda", 11.22, 22.22, 1L, 1L);
        var tripInfo = new TripDataDto("Raclawica", LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22), -20.22, 11.11);
        var userTransport = new UserTransport(3L, Duration.ofHours(9),
                BigDecimal.ONE,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link",
                "train",
                "Desc",
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(10, 0)));

        var airTransport = new AirTransport(1L,
                Duration.ofHours(2),
                BigDecimal.TEN,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link",
                flights);

        var carTransport = new CarTransport(2L,
                Duration.ofHours(7),
                123L,
                BigDecimal.TEN,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link");

        //when
        when(accommodationTransportService.findAccommodationTransport(anyLong())).thenReturn(accommodationTransport);
        when(accommodationProxy.getAccommodationInfo(anyString(), anyLong())).thenReturn(accommodationInfo);
        when(tripGroupProxy.getTripData(anyString(), anyLong())).thenReturn(tripInfo);
        when(transportRepository.findUserTransport(anyList())).thenReturn(List.of(userTransport));
        when(transportRepository.findAirTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findCarTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findMatchingAirTransport(anyString(), anyString(), any())).thenReturn(List.of(airTransport));
        when(transportRepository.findMatchingCarTransport(anyString(), anyString(), any())).thenReturn(List.of(carTransport));
        var actualResult = transportService.getTransportForAccommodation(1L);

        //then
        var expectedResult = List.of(airTransport, carTransport, userTransport);
        assertThat(actualResult).hasSameElementsAs(expectedResult);
        verify(accommodationTransportService, times(1)).findAccommodationTransport(anyLong());
        verify(accommodationTransportService, times(2)).createAccommodationTransport(anyLong(), anyLong());
        verify(accommodationProxy, times(1)).getAccommodationInfo(anyString(), anyLong());
        verify(tripGroupProxy, times(1)).getTripData(anyString(), anyLong());
        verify(transportRepository, times(1)).findUserTransport(anyList());
        verify(transportRepository, times(1)).findAirTransport(anyList());
        verify(transportRepository, times(1)).findCarTransport(anyList());
        verify(transportRepository, times(1)).findMatchingAirTransport(anyString(), anyString(), any());
        verify(transportRepository, times(1)).findMatchingCarTransport(anyString(), anyString(), any());
        verify(lufthansaAdapter, never()).generateTransportAir(any(), any());
    }

    @Test
    void shouldGetTransportForAccommodationWhenAirTransportsNeedsToBeGenerated() {
        //given
        var flights = List.of(new Flight(1L, "LH123", "WRO", "PMI",
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(13,33)),
                LocalDateTime.of(LocalDate.of(2022, 11, 15), LocalTime.of(15,33)),
                Duration.ofSeconds(7200)));

        var accommodationTransport = List.of(new AccommodationTransport(new AccommodationTransportId(1L, 1L)),
                new AccommodationTransport(new AccommodationTransportId(2L, 2L)),
                new AccommodationTransport(new AccommodationTransportId(3L, 3L)));
        var accommodationInfo = new AccommodationInfoDto("Mallorcas", "Mallorca", 11.22, 22.22, 1L, 1L);
        var tripInfo = new TripDataDto("Wroclaw", LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22), -20.22, 11.11);

        var airTransport = new AirTransport(1L,
                Duration.ofHours(2),
                BigDecimal.ZERO,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link",
                flights);

        var carTransport = new CarTransport(2L,
                Duration.ofHours(7),
                123L,
                BigDecimal.TEN,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link");

        TreeMap<Long, List<Flight>> flightTreeMap = new TreeMap<>();
        flightTreeMap.put(7200L, flights);

        //when
        when(accommodationTransportService.findAccommodationTransport(anyLong())).thenReturn(accommodationTransport);
        when(accommodationProxy.getAccommodationInfo(anyString(), anyLong())).thenReturn(accommodationInfo);
        when(tripGroupProxy.getTripData(anyString(), anyLong())).thenReturn(tripInfo);
        when(transportRepository.findUserTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findAirTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findCarTransport(anyList())).thenReturn(List.of(carTransport));
        when(transportRepository.findMatchingAirTransport(anyString(), anyString(), any())).thenReturn(List.of());
        when(lufthansaAdapter.generateTransportAir(any(), any())).thenReturn(flightTreeMap);
        when(transportRepository.save(any(AirTransport.class))).thenAnswer(i -> i.getArguments()[0]);

        var actualResult = transportService.getTransportForAccommodation(1L);

        //then
        assertThat(actualResult.get(0)).satisfies(
                at -> {
                    assertThat(at.getSource()).isEqualTo(airTransport.getSource());
                    assertThat(at.getDestination()).isEqualTo(airTransport.getDestination());
                    assertThat(at.getDuration()).isEqualTo(airTransport.getDuration());
                    assertThat(at.getStartDate()).isEqualTo(airTransport.getStartDate());
                    assertThat(at.getEndDate()).isEqualTo(airTransport.getEndDate());
                    assertThat(at.getPrice()).isEqualTo(airTransport.getPrice());
                }
        );
        verify(accommodationTransportService, times(1)).findAccommodationTransport(any());
        verify(accommodationTransportService, times(1)).createAccommodationTransport(any(), any());
        verify(accommodationProxy, times(1)).getAccommodationInfo(anyString(), anyLong());
        verify(tripGroupProxy, times(1)).getTripData(anyString(), anyLong());
        verify(transportRepository, times(1)).findUserTransport(anyList());
        verify(transportRepository, times(1)).findAirTransport(anyList());
        verify(transportRepository, times(1)).findCarTransport(anyList());
        verify(transportRepository, times(1)).findMatchingAirTransport(anyString(), anyString(), any());
        verify(transportRepository, never()).findMatchingCarTransport(anyString(), anyString(), any());
        verify(lufthansaAdapter,times(1)).generateTransportAir(any(), any());
        verify(geoLocationAdapter,never()).getRoute(any(), any());
        verify(geoLocationAdapter,never()).getDuration(any());
        verify(geoLocationAdapter,never()).getDistance(any());

    }

    @Test
    void shouldGetTransportForAccommodationWhenCarTransportsNeedsToBeGenerated() {
        //given
        var accommodationTransport = List.of(new AccommodationTransport(new AccommodationTransportId(1L, 1L)),
                new AccommodationTransport(new AccommodationTransportId(2L, 2L)),
                new AccommodationTransport(new AccommodationTransportId(3L, 3L)));
        var accommodationInfo = new AccommodationInfoDto("Mallorca", "Mallorca", 11.22, 22.22, 1L, 1L);
        var tripInfo = new TripDataDto("Wroclaw", LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22), -20.22, 11.11);

        var carTransport = new CarTransport(2L,
                Duration.ofHours(7),
                123L,
                BigDecimal.TEN,
                "Wroclaw",
                "Mallorca",
                LocalDate.of(2022, 11, 15),
                LocalDate.of(2022, 11, 22),
                "link");


        //when
        when(accommodationTransportService.findAccommodationTransport(anyLong())).thenReturn(accommodationTransport);
        when(accommodationProxy.getAccommodationInfo(anyString(), anyLong())).thenReturn(accommodationInfo);
        when(tripGroupProxy.getTripData(anyString(), anyLong())).thenReturn(tripInfo);
        when(transportRepository.findUserTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findAirTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findCarTransport(anyList())).thenReturn(List.of());
        when(transportRepository.findMatchingCarTransport(anyString(), anyString(), any())).thenReturn(List.of());
        when(transportRepository.findMatchingAirTransport(anyString(), anyString(), any())).thenReturn(List.of());
        when(lufthansaAdapter.generateTransportAir(any(),any())).thenReturn(null);
        when(geoLocationAdapter.getRoute(anyString(), anyString())).thenReturn(null);
        when(geoLocationAdapter.getDistance(any())).thenReturn(123L);
        when(geoLocationAdapter.getDuration(any())).thenReturn(Duration.ofHours(7));
        when(transportRepository.save(any(CarTransport.class))).thenAnswer(i -> i.getArguments()[0]);

        var actualResult = transportService.getTransportForAccommodation(1L);

        //then
        CarTransport actualCarTransport = (CarTransport) actualResult.get(0);
        assertThat(actualCarTransport).satisfies(
                at -> {
                    assertThat(at.getDistanceInKm()).isEqualTo(carTransport.getDistanceInKm());
                    assertThat(at.getDuration()).isEqualTo(carTransport.getDuration());
                    assertThat(at.getDestination()).isEqualTo(carTransport.getDestination());
                    assertThat(at.getSource()).isEqualTo(carTransport.getSource());
                }
        );
        verify(accommodationTransportService, times(1)).findAccommodationTransport(any());
        verify(accommodationTransportService, times(1)).createAccommodationTransport(any(), any());
        verify(accommodationProxy, times(1)).getAccommodationInfo(anyString(), anyLong());
        verify(tripGroupProxy, times(1)).getTripData(anyString(), anyLong());
        verify(transportRepository, times(1)).findUserTransport(anyList());
        verify(transportRepository, times(1)).findAirTransport(anyList());
        verify(transportRepository, times(1)).findCarTransport(anyList());
        verify(transportRepository, times(1)).findMatchingAirTransport(anyString(), anyString(), any());
        verify(transportRepository, times(1)).findMatchingCarTransport(anyString(), anyString(), any());
        verify(lufthansaAdapter,times(1)).generateTransportAir(any(), any());
        verify(geoLocationAdapter,times(1)).getDuration(any());
        verify(geoLocationAdapter,times(1)).getDistance(any());
        verify(geoLocationAdapter,times(1)).getRoute(any(), any());
    }

    @Test
    void shouldCorrectlyCreateUserTransport() {
        //given
        mockAuthorizeCreatorOrCoordinatorAspect();
        var userTransportDto = new UserTransportDto(Duration.ZERO, BigDecimal.ONE, "source", "dest",
                LocalDate.now(), LocalDate.now(), "mean", "desc", LocalDateTime.now(), "link");

        //when
        when(transportRepository.save(any(UserTransport.class))).thenAnswer(i -> i.getArguments()[0]);
        var result = transportService.createUserTransport(1L, userTransportDto);

        //then
        var expectedTransport = new UserTransport(Duration.ZERO,
                BigDecimal.ONE,
                "source",
                "dest",
                LocalDate.now(),
                LocalDate.now(),
                "link",
                "mean",
                "desc",
                LocalDateTime.now());

        assertThat(result).satisfies(
                ut -> {
                    assertThat(ut.getDescription()).isEqualTo(expectedTransport.getDescription());
                    assertThat(ut.getPrice()).isEqualTo(expectedTransport.getPrice());
                    assertThat(ut.getDuration()).isEqualTo(expectedTransport.getDuration());
                    assertThat(ut.getSource()).isEqualTo(expectedTransport.getSource());
                    assertThat(ut.getDestination()).isEqualTo(expectedTransport.getDestination());
                    assertThat(ut.getMeanOfTransport()).isEqualTo(expectedTransport.getMeanOfTransport());
                }
        );
        verify(transportRepository, times(1)).save(any());
        verify(accommodationTransportService, times(1)).createAccommodationTransport(any(), any());

    }

    @Test
    void shouldDeleteUserTransport() {
        //given
        mockAuthorizeCreatorOrCoordinatorAspect();

        //when
        transportService.deleteUserTransport(1L, 1L);

        //then
        verify(transportRepository, times(1)).deleteById(1L);
    }
}