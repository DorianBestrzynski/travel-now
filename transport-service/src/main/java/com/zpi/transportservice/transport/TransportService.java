package com.zpi.transportservice.transport;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.zpi.transportservice.adapter.GeoLocationAdapter;
import com.zpi.transportservice.adapter.LufthansaAdapter;
import com.zpi.transportservice.aspects.AuthorizeCreatorOrCoordinator;
import com.zpi.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.dto.TripDataDto;
import com.zpi.transportservice.dto.UserTransportDto;
import com.zpi.transportservice.flight.Flight;
import com.zpi.transportservice.flight.FlightService;
import com.zpi.transportservice.mapper.MapStructMapper;
import com.zpi.transportservice.accommodation_transport.AccommodationTransportService;
import com.zpi.transportservice.proxy.AccommodationProxy;
import com.zpi.transportservice.proxy.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransportService {
    private final TransportRepository transportRepository;
    private final AccommodationTransportService accommodationTransportService;
    private final TripGroupProxy tripGroupProxy;
    private final AccommodationProxy accommodationProxy;
    private final LufthansaAdapter lufthansaAdapter;
    private final FlightService flightService;
    private final MapStructMapper mapper;
    private final GeoLocationAdapter geoLocationAdapter;
    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    @Transactional
    public List<Transport> getTransportForAccommodation(Long accommodationId) {
        List<Transport> transports = new ArrayList<>();
        var accommodationTransportIdList = accommodationTransportService.findAccommodationTransport(accommodationId)
                                                                        .stream()
                                                                        .map(accommodationTransport -> accommodationTransport.getId().getTransportId())
                                                                        .toList();
        var accommodationInfo = accommodationProxy.getAccommodationInfo(INNER_COMMUNICATION, accommodationId);
        var tripInfo = tripGroupProxy.getTripData(INNER_COMMUNICATION, accommodationInfo.groupId());
        var userTransport = getUserTransport(accommodationTransportIdList);
        var airTransport = generateAirTransportForAccommodation(accommodationTransportIdList, accommodationInfo,
                                                                tripInfo, accommodationId);
        var carTransport = generateCarTransportForAccommodation(accommodationTransportIdList, accommodationInfo,
                                                                tripInfo, accommodationId);
        if (airTransport != null) {
            transports.add(airTransport);
        }

        if (carTransport != null) {
            transports.add(carTransport);
        }
        transports.addAll(userTransport);
        return transports;
    }

    private CarTransport generateCarTransportForAccommodation(List<Long> accommodationTransportIdList,
                                                              AccommodationInfoDto accommodationInfo,
                                                              TripDataDto tripData, Long accommodationId) {
        var carTransport = shouldGenerateCarTransport(accommodationTransportIdList, accommodationInfo, tripData,
                                                      accommodationId);
        if (carTransport == null) {
            try {
                var route = geoLocationAdapter.getRoute(tripData.startingLocation(), accommodationInfo.streetAddress());
                var distance = geoLocationAdapter.getDistance(route);
                var duration = geoLocationAdapter.getDuration(route);
                var endDate = tripData.startDate().plus(duration.toDays(), ChronoUnit.DAYS);

                var newCarTransport = new CarTransport(duration, distance, null, tripData.startingLocation(),
                                                       accommodationInfo.streetAddress(), tripData.startDate(), endDate,
                                                       null);
                var carTransportSaved = transportRepository.save(newCarTransport);
                accommodationTransportService.createAccommodationTransport(accommodationId, carTransportSaved.getTransportId());

                return carTransportSaved;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return carTransport;
    }

    private CarTransport shouldGenerateCarTransport(List<Long> accommodationTransportIds,
                                                    AccommodationInfoDto accommodationInfo, TripDataDto tripData,
                                                    Long accommodationId) {
        var carTransports = transportRepository.findCarTransport(accommodationTransportIds);
        if (carTransports.isEmpty()) {
            var matchingAccommodationCarTransport = transportRepository.findMatchingCarTransport(
                    tripData.startingLocation(),
                    accommodationInfo.city(), tripData.startDate());
            if (matchingAccommodationCarTransport.isEmpty()) {
                return null;
            }
            accommodationTransportService.createAccommodationTransport(accommodationId,
                                                                       matchingAccommodationCarTransport.get(0)
                                                                                                        .getTransportId());
            return matchingAccommodationCarTransport.get(0);
        }
        return carTransports.get(0);
    }

    private List<UserTransport> getUserTransport(List<Long> transportList) {
        return transportRepository.findUserTransport(transportList);
    }

    private AirTransport generateAirTransportForAccommodation(List<Long> accommodationTransportIds,
                                                             AccommodationInfoDto accommodationInfo,
                                                             TripDataDto tripData, Long accommodationId) {

        var transportAir = shouldGenerateAirTransport(accommodationTransportIds, accommodationInfo, tripData, accommodationId);
        if (transportAir == null) {
            var flightProposals = lufthansaAdapter.generateTransportAir(accommodationInfo, tripData);
            return selectBestFlight(flightProposals, accommodationId, accommodationInfo, tripData);
        }
        return transportAir;
    }

    private AirTransport selectBestFlight(TreeMap<Long, List<Flight>> flightProposals, Long accommodationId,
                                          AccommodationInfoDto accommodationInfoDto, TripDataDto tripData) {
        if (flightProposals != null && !flightProposals.isEmpty()) {
            var bestOption = flightProposals.firstEntry();
            var totalDuration = Duration.ofSeconds(bestOption.getKey());
            var flights = bestOption.getValue();
            AirTransport airTransport = new AirTransport(totalDuration, BigDecimal.ZERO, tripData.startingLocation(),
                                                         accommodationInfoDto.city(), tripData.startDate(),
                                                         tripData.endDate(), "Why link", flights);
            flightService.setFlights(flights, airTransport);
            var airTransportSaved = transportRepository.save(airTransport);
            accommodationTransportService.createAccommodationTransport(accommodationId, airTransportSaved.getTransportId());
            return airTransport;
        }
        return null;
    }

    private AirTransport shouldGenerateAirTransport(List<Long> accommodationTransportIds,
                                                    AccommodationInfoDto accommodationInfo, TripDataDto tripData,
                                                    Long accommodationId) {
        var transportAir = transportRepository.findAirTransport(accommodationTransportIds);
        if (transportAir.isEmpty()) {
            var matchingAccommodationTransportAir = transportRepository.findMatchingAirTransport(tripData.startingLocation(), accommodationInfo.city(), tripData.startDate());
            if (matchingAccommodationTransportAir.isEmpty()) {
                return null;
            }
            accommodationTransportService.createAccommodationTransport(accommodationId, matchingAccommodationTransportAir.get(0)
                                                                                                        .getTransportId());
            return matchingAccommodationTransportAir.get(0);
        }
        return transportAir.get(0);
    }

    @Transactional
    @AuthorizeCreatorOrCoordinator
    public UserTransport createUserTransport(Long accommodationId, @Valid UserTransportDto userTransportDto) {
        UserTransport userTransport = new UserTransport(userTransportDto.duration(), userTransportDto.price(),
                                                        userTransportDto.source(),
                                                        userTransportDto.destination(), userTransportDto.startDate(),
                                                        userTransportDto.endDate(), userTransportDto.link(),
                                                        userTransportDto.meanOfTransport(),
                                                        userTransportDto.description(), userTransportDto.meetingTime());
        var userTransportSaved = transportRepository.save(userTransport);
        accommodationTransportService.createAccommodationTransport(accommodationId,
                                                                   userTransportSaved.getTransportId());
        return userTransportSaved;
    }

    @Transactional
    @AuthorizeCreatorOrCoordinator
    public void deleteUserTransport(Long accommodationId, Long transportId) {
        transportRepository.deleteById(transportId);
    }
}
