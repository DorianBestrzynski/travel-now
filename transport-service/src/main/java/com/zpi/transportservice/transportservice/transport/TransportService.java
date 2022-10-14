package com.zpi.transportservice.transportservice.transport;

import com.zpi.transportservice.transportservice.accommodation_transport.AccommodationTransportService;
import com.zpi.transportservice.transportservice.adapter.LufthansaAdapter;
import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.transportservice.dto.TripDataDto;
import com.zpi.transportservice.transportservice.flight.Flight;
import com.zpi.transportservice.transportservice.flight.FlightService;
import com.zpi.transportservice.transportservice.proxy.AccommodationProxy;
import com.zpi.transportservice.transportservice.proxy.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TransportService {
    private final TransportRepository transportRepository;
    private final AirTransportRepository airTransportRepository;
    private final AccommodationTransportService accommodationTransportService;
    private final TripGroupProxy tripGroupProxy;
    private final AccommodationProxy accommodationProxy;
    private final LufthansaAdapter lufthansaAdapter;
    private final FlightService flightService;

    @Transactional
    public List<Transport> getTransportForAccommodation(Long accommodationId){
        List<Transport> transports = new ArrayList<>();
        var accommodationTransportIdList = accommodationTransportService.findAccommodationTransport(accommodationId)
                .stream()
                .map(accommodationTransport -> accommodationTransport.getId().getTransportId())
                .toList();
        var accommodationInfo = accommodationProxy.getAccommodationInfo(accommodationId);
        var tripInfo = tripGroupProxy.getTripData(accommodationInfo.groupId());
        var airTransport = generateTransportForAccommodationAir(accommodationTransportIdList, accommodationInfo, tripInfo, accommodationId);
        //TODO here car transport logic and return type List<Transport> with best AirTransport and CarTransport
        if(airTransport != null){
            transports.add(airTransport);
        }
        return transports;
    }

    public AirTransport generateTransportForAccommodationAir(List<Long> accommodationTransportIds, AccommodationInfoDto accommodationInfo, TripDataDto tripData, Long accommodationId) {
        var transportAir = shouldGenerateTransportAir(accommodationTransportIds, accommodationInfo, tripData, accommodationId);
        if(transportAir == null){
            var flightProposals = lufthansaAdapter.generateTransportAir(accommodationInfo, tripData);
            return selectBestFlight(flightProposals, accommodationId, accommodationInfo, tripData);
        }
        return transportAir;
    }

    private AirTransport selectBestFlight(TreeMap<Long, List<Flight>> flightProposals, Long accommodationId, AccommodationInfoDto accommodationInfoDto, TripDataDto tripData) {
        if(flightProposals != null){
            var bestOption = flightProposals.firstEntry();
            var totalDuration = Duration.ofSeconds(bestOption.getKey());
            var flights = bestOption.getValue();
            AirTransport airTransport = new AirTransport(totalDuration, BigDecimal.ZERO, tripData.startingLocation(), accommodationInfoDto.city(), tripData.startDate(), tripData.endDate(), "Why link", flights);
            flightService.setFlights(flights, airTransport);
            var airTransportSaved = transportRepository.save(airTransport);
            accommodationTransportService.createAccommodationTransport(accommodationId, airTransportSaved.getTransportId());
            return airTransport;
        }
        return null;
    }

    private AirTransport shouldGenerateTransportAir(List<Long> accommodationTransportIds, AccommodationInfoDto accommodationInfo, TripDataDto tripData, Long accommodationId) {
        var transportAir = airTransportRepository.findAllById(accommodationTransportIds);
        if(transportAir.isEmpty()) {
            var matchingAccommodationTransportAir = transportRepository.findMatchingTransportAir(tripData.startingLocation(),
                    accommodationInfo.city(), tripData.startDate());
            if(matchingAccommodationTransportAir.isEmpty()) {
                return null;
            }
            accommodationTransportService.createAccommodationTransport(accommodationId, matchingAccommodationTransportAir.get(0).getTransportId());
            return matchingAccommodationTransportAir.get(0);
        }
        return transportAir.get(0);
    }



}
