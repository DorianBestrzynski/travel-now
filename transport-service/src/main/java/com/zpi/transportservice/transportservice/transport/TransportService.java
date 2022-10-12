package com.zpi.transportservice.transportservice.transport;

import com.zpi.transportservice.transportservice.accommodation_transport.AccommodationTransportService;
import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.transportservice.dto.AirportInfoDto;
import com.zpi.transportservice.transportservice.dto.TripDataDto;
import com.zpi.transportservice.transportservice.exception.LufthansaApiException;
import com.zpi.transportservice.transportservice.flight.Flight;
import com.zpi.transportservice.transportservice.flight.FlightService;
import com.zpi.transportservice.transportservice.proxy.AccommodationProxy;
import com.zpi.transportservice.transportservice.proxy.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

import static com.zpi.transportservice.transportservice.commons.Constants.*;
import static com.zpi.transportservice.transportservice.exception.ExceptionsInfo.LUFTHANSA_API_EXCEPTION;
import static com.zpi.transportservice.transportservice.exception.ExceptionsInfo.LUFTHANSA_NO_AIRPORT_MATCHING;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportService {
    private final TransportRepository transportRepository;
    private final AccommodationTransportService accommodationTransportService;
    private final RestTemplate restTemplate;
    private final TripGroupProxy tripGroupProxy;
    private final AccommodationProxy accommodationProxy;
    private final FlightService flightService;
    private static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm";

    private String bearerAccessToken = "Bearer un5hpkj546n88cnkndyhnbdx";
    @Value("${client_id}")
    private String client_id;
    @Value("${client_secret}")
    private String client_secret;
    @Value("${grant_type}")
    private String grant_type;


    @Transactional
    public AirTransport getTransportForAccommodation(Long accommodationId){
        var accommodationTransportIdList = accommodationTransportService.findAccommodationTransport(accommodationId)
                .stream()
                .map(accommodationTransport -> accommodationTransport.getId().getTransportId())
                .toList();
        var accommodationInfo = accommodationProxy.getAccommodationInfo(accommodationId);
        var tripInfo = tripGroupProxy.getTripData(accommodationInfo.groupId());

        var airTransport = generateTransportForAccommodationAir(accommodationTransportIdList, accommodationInfo, tripInfo, accommodationId);
        System.out.println(airTransport.getFlight());
        return airTransport;
    }

    public AirTransport generateTransportForAccommodationAir(List<Long> accommodationTransportIds, AccommodationInfoDto accommodationInfo, TripDataDto tripData, Long accommodationId) {
        var transportAir = shouldGenerateTransportAir(accommodationTransportIds, accommodationInfo, tripData, accommodationId);
        if(transportAir == null){
            return generateTransportAir(accommodationInfo, tripData);
        }
        return transportAir;
    }

    private AirTransport shouldGenerateTransportAir(List<Long> accommodationTransportIds, AccommodationInfoDto accommodationInfo, TripDataDto tripData, Long accommodationId) {
        var transportAir = transportRepository.findTransportAir(accommodationTransportIds);
        if(transportAir.isEmpty()) {
            var matchingAccommodationTransportAir = transportRepository.findMatchingTransportAir(tripData.startingLocation(),
                    accommodationInfo.streetAddress(), tripData.startDate());
            if(matchingAccommodationTransportAir.isEmpty()){
                return null;
            }
            accommodationTransportService.createAccommodationTransport(accommodationId, matchingAccommodationTransportAir.get(0).getTransportId());
            return matchingAccommodationTransportAir.get(0);
        }
        return transportAir.get(0);
    }

    private AirTransport generateTransportAir(AccommodationInfoDto accommodationInfoDto, TripDataDto tripData) {
        var nearestAirportSource = findNearestAirport(tripData.latitude(), tripData.longitude());
        var nearestAirportDestination = findNearestAirport(accommodationInfoDto.destinationLatitude(), accommodationInfoDto.destinationLongitude());
        var flightProposals = findFlightProposals(nearestAirportSource, nearestAirportDestination, tripData.startDate());
        if(flightProposals != null){
            var bestOption = chooseBestFlight(flightProposals);
            var totalDuration = Duration.ofSeconds(bestOption.getKey());
            var flights = flightProposals.get(bestOption.getValue());
            AirTransport airTransport = new AirTransport(totalDuration, BigDecimal.ZERO, tripData.startingLocation(), accommodationInfoDto.streetAddress(), tripData.startDate(), tripData.endDate(), "Why link", flights);
//            transportRepository.save(airTransport);
            return airTransport;
        }
        return null;
    }

    private Map.Entry<Long,Integer> chooseBestFlight(List<List<Flight>> flightProposals) {
        TreeMap<Long, Integer> flightMap = new TreeMap<>();
       for(var flightProposal : flightProposals) {
           var totalDuration = flightProposal.stream().mapToLong(flight -> flight.getFlightDuration().getSeconds()).sum();
           flightMap.put(totalDuration, flightProposals.indexOf(flightProposal));
       }
       return flightMap.firstEntry();
    }

    private List<AirportInfoDto> findNearestAirport(Double latitude, Double longitude) {
        try {
            HttpEntity<?> entity = getHttpEntity();
            var url = BASE_URL + NEAREST_AIRPORT + latitude + "," + longitude;
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class);

            JSONObject nearestAirportResource = new JSONObject(response.getBody());
            var airports = (JSONArray) nearestAirportResource.query("/NearestAirportResource/Airports/Airport");

            List<AirportInfoDto> airportInfo = new ArrayList<>();
            for (int x = 0; x < airports.length(); x++) {
                var airportCode = airports.getJSONObject(x).getString("AirportCode");
                var distance = (JSONObject) airports.getJSONObject(x).query("/Distance");
                var actualDistance = distance.getInt("Value");

                airportInfo.add(new AirportInfoDto(airportCode, actualDistance));
            }

            return airportInfo;
        } catch (Exception ex) {
            throw new LufthansaApiException(LUFTHANSA_API_EXCEPTION);
        }
    }

    @NotNull
    private HttpEntity<?> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",bearerAccessToken);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }

    private List<List<Flight>> findFlightProposals(List<AirportInfoDto> nearestAirportSource,List<AirportInfoDto> nearestAirportDestination, LocalDate startDate) {
        try {
            HttpEntity<?> entity = getHttpEntity();
            if(nearestAirportSource.isEmpty() || nearestAirportDestination.isEmpty()){
                throw new LufthansaApiException(LUFTHANSA_NO_AIRPORT_MATCHING);
            }
            var sourceAirportsCloserThanThreshold = onlyAirportsToGivenThreshold(nearestAirportSource, THRESHOLD_DISTANCE_SOURCE);
            var departureAirportsCloserThanThreshold = onlyAirportsToGivenThreshold(nearestAirportDestination, THRESHOLD_DISTANCE_DESTINATION);

            StringBuilder url = new StringBuilder(BASE_URL + FLIGHT_SCHEDULES);
            return findPossibleFlights(sourceAirportsCloserThanThreshold, departureAirportsCloserThanThreshold, url, startDate, entity);

        } catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

    }

    private List<List<Flight>> findPossibleFlights(List<AirportInfoDto> startAirports, List<AirportInfoDto> destinationAirports, StringBuilder url, LocalDate startDate, HttpEntity<?> entity) {
        List<List<Flight>> allPossibleFlights = new ArrayList<>();
        for (var sourceAirport : startAirports){
            for (var destinationAirport : destinationAirports) {
                String finalUrl = url.toString() + sourceAirport.airportCode() + "/" + destinationAirport.airportCode() + "/" + startDate;
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                            finalUrl,
                            HttpMethod.GET,
                            entity,
                            String.class);

                    var flightProposal = parseResponseToFlight(response.getBody());
                    allPossibleFlights.add(flightProposal);

                } catch (Exception ex) {
                    log.warn("Flight not found");
                }

            }
        }
        return allPossibleFlights;
    }

    private List<Flight> parseResponseToFlight(String flightSchedule) {
        List<Flight> flightList = new ArrayList<>();
        JSONObject flightSchedules = new JSONObject(flightSchedule);
        var scheduleResource = flightSchedules.getJSONObject("ScheduleResource").getJSONArray("Schedule");
        var flightGeneralInfo = scheduleResource.getJSONObject(0).getJSONArray("Flight");
        for (int i = 0; i < flightGeneralInfo.length(); i++) {
            var innerFlight = flightGeneralInfo.getJSONObject(i);
            var departure = innerFlight.getJSONObject("Departure");
            var sourceAirportCode = departure.getString("AirportCode");
            var departureTime = departure.getJSONObject("ScheduledTimeLocal").getString("DateTime");
            var arrival = innerFlight.getJSONObject("Arrival");
            var destinationAirportCode = arrival.getString("AirportCode");
            var arrivalTime = arrival.getJSONObject("ScheduledTimeLocal").getString("DateTime");
            var marketingCarrier = innerFlight.getJSONObject("MarketingCarrier");
            var airlineId = marketingCarrier.getString("AirlineID");
            var flightNumber = marketingCarrier.getString("FlightNumber");
            var formatter = new DateTimeFormatterBuilder().appendPattern(ISO_8601_24H_FULL_FORMAT).toFormatter();
            var departureTimeConverted = LocalDateTime.from(formatter.parse(departureTime));
            var arrivalTimeConverted = LocalDateTime.from(formatter.parse(arrivalTime));
            var seconds = Duration.between(departureTimeConverted, arrivalTimeConverted).getSeconds();
            var duration = Duration.ofSeconds(seconds);
            Flight flight = new Flight(airlineId + flightNumber, sourceAirportCode, destinationAirportCode, departureTimeConverted, arrivalTimeConverted, duration);
            flightList.add(flight);

        }

        return flightList;

    }

    private List<AirportInfoDto> onlyAirportsToGivenThreshold(List<AirportInfoDto> airportInfo, Integer thresholdDistance){
        return airportInfo.stream()
                .filter(airport -> airport.distance() < thresholdDistance)
                .toList();
    }

    private void generateAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add(CLIENT_ID , client_id);
            map.add(CLIENT_SECRET, client_secret);
            map.add(GRANT_TYPE, grant_type);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(BASE_URL + "/v1/oauth/token",
                            HttpMethod.POST,
                            entity,
                            String.class);
            JSONObject json = new JSONObject(response.getBody());

            this.bearerAccessToken = "Bearer " + json.getString(ACCESS_TOKEN);

        }catch (Exception ex){
            throw new RuntimeException("Exception while getting Lufthansa access token");
        }
    }

}
