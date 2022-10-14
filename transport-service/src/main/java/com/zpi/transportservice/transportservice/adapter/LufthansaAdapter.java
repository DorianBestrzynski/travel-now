package com.zpi.transportservice.transportservice.adapter;

import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.transportservice.dto.AirportInfoDto;
import com.zpi.transportservice.transportservice.dto.TripDataDto;
import com.zpi.transportservice.transportservice.exception.LufthansaApiException;
import com.zpi.transportservice.transportservice.flight.Flight;
import com.zpi.transportservice.transportservice.flight.FlightService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.zpi.transportservice.transportservice.commons.Constants.*;
import static com.zpi.transportservice.transportservice.exception.ExceptionsInfo.LUFTHANSA_API_EXCEPTION;
import static com.zpi.transportservice.transportservice.exception.ExceptionsInfo.LUFTHANSA_NO_AIRPORT_MATCHING;


@Getter
@RequiredArgsConstructor
@Slf4j
@Component
public class LufthansaAdapter {
    private final RestTemplate restTemplate;
    private Instant tokenExpirationDate;
    private final FlightService flightService;
    private static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private String bearerAccessToken = "Bearer 2t85hmbeqy5vv7axkg4wy8gg";
    @Value("${client_id}")
    private String client_id;
    @Value("${client_secret}")
    private String client_secret;
    @Value("${grant_type}")
    private String grant_type;

    public TreeMap<Long, List<Flight>> generateTransportAir(AccommodationInfoDto accommodationInfoDto, TripDataDto tripData) {
        if(isTokenExpiredEx()){
            generateAccessToken();
        }
        var nearestAirportSource = findNearestAirport(tripData.latitude(), tripData.longitude());
        var nearestAirportDestination = findNearestAirport(accommodationInfoDto.destinationLatitude(), accommodationInfoDto.destinationLongitude());
        return findFlightProposals(nearestAirportSource, nearestAirportDestination, tripData.startDate());
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

    private TreeMap<Long,List<Flight>> findFlightProposals(List<AirportInfoDto> nearestAirportSource, List<AirportInfoDto> nearestAirportDestination, LocalDate startDate) {
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

    private TreeMap<Long,List<Flight>> findPossibleFlights(List<AirportInfoDto> startAirports, List<AirportInfoDto> destinationAirports, StringBuilder url, LocalDate startDate, HttpEntity<?> entity) {
        TreeMap<Long, List<Flight>> allPossibleFlights = new TreeMap<>();
        for (var sourceAirport : startAirports){
            for (var destinationAirport : destinationAirports) {
                String finalUrl = url.toString() + sourceAirport.airportCode() + "/" + destinationAirport.airportCode() + "/" + startDate;
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(finalUrl).queryParam("limit", FLIGHT_PROPOSAL_LIMIT);
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            entity,
                            String.class);

                    var flightProposal = parseResponseToFlight(response.getBody());
                    allPossibleFlights.put(flightProposal.getKey(), flightProposal.getValue());

                } catch (Exception ex) {
                    log.warn("Flight not found");
                }

            }
        }
        return allPossibleFlights;
    }

    private Map.Entry<Long, List<Flight>> parseResponseToFlight(String flightSchedule) {
        List<Flight> flightList = new ArrayList<>();
        JSONObject flightSchedules = new JSONObject(flightSchedule);
        var scheduleResource = flightSchedules.getJSONObject("ScheduleResource").getJSONObject("Schedule");
        var durationString = scheduleResource.getJSONObject("TotalJourney").getString("Duration");
        Duration totalDuration = Duration.parse(durationString);
        var flightGeneralInfo = scheduleResource.getJSONArray("Flight");
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
        return Map.entry(totalDuration.getSeconds(), flightList);
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
            var expirationTime = json.getLong(EXPIRATION);
            this.tokenExpirationDate = Instant.now().plusSeconds(expirationTime);

        }catch (Exception ex){
            throw new RuntimeException("Exception while getting Lufthansa access token");
        }
    }

    private boolean isTokenExpired() {
        return tokenExpirationDate == null || !Instant.now().isBefore(tokenExpirationDate);
    }

    private boolean isTokenExpiredEx(){
        try {
            HttpEntity<?> entity = getHttpEntity();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String url = BASE_URL + COUNTRY_QUERY + DENMARK;
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("limit", COUNTRY_LIMIT);
            restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);

            return false;
        } catch (Exception ex){
            return true;
        }

    }
}
