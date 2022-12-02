package com.zpi.transportservice.adapter;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.zpi.transportservice.commons.Constants;
import com.zpi.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.dto.AirportInfoDto;
import com.zpi.transportservice.dto.TripDataDto;
import com.zpi.transportservice.exception.LufthansaApiException;
import com.zpi.transportservice.flight.Flight;
import com.zpi.transportservice.flight.FlightService;
import com.zpi.transportservice.lufthansa.LufthansaKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.zpi.transportservice.exception.ExceptionsInfo.LUFTHANSA_API_EXCEPTION;
import static com.zpi.transportservice.exception.ExceptionsInfo.LUFTHANSA_NO_AIRPORT_MATCHING;


@Getter
@RequiredArgsConstructor
@Slf4j
@Component
@PropertySource("classpath:application-keys.yml")
public class LufthansaAdapter {
    private final RestTemplate restTemplate;
    private Instant tokenExpirationDate;
    private final FlightService flightService;
    private final LufthansaKey lufthansaKey;

    private final GeoApiContext context;
    private static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm";
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

        var nearestAirportSource = findNearestAirport(tripData.latitude(), tripData.longitude(), false);
        var nearestAirportDestination = findNearestAirport(accommodationInfoDto.destinationLatitude(), accommodationInfoDto.destinationLongitude(), false);
        return findFlightProposals(nearestAirportSource, nearestAirportDestination, tripData, accommodationInfoDto);


    }

    private List<AirportInfoDto> findNearestAirport(Double latitude, Double longitude, boolean retry) {
        try {
            HttpEntity<?> entity = getHttpEntity();
            var url = Constants.BASE_URL + Constants.NEAREST_AIRPORT + latitude + "," + longitude;
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
            if(retry) {
                throw new LufthansaApiException(LUFTHANSA_API_EXCEPTION);
            }
            return findNearestAirport(latitude, longitude, true);
        }
    }

    @NotNull
    private HttpEntity<?> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",lufthansaKey.getLufthansaToken());
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }

    private TreeMap<Long, List<Flight>> findFlightProposals(List<AirportInfoDto> nearestAirportSource, List<AirportInfoDto> nearestAirportDestination, TripDataDto tripInfo, AccommodationInfoDto accommodationInfoDto) {
        try {
            HttpEntity<?> entity = getHttpEntity();

            var sourceAirportsCloserThanThreshold = onlyAirportsToGivenThreshold(nearestAirportSource, Constants.THRESHOLD_DISTANCE_SOURCE);
            var departureAirportsCloserThanThreshold = onlyAirportsToGivenThreshold(nearestAirportDestination, Constants.THRESHOLD_DISTANCE_DESTINATION);

            if(sourceAirportsCloserThanThreshold.isEmpty() || departureAirportsCloserThanThreshold.isEmpty()){
                throw new LufthansaApiException(LUFTHANSA_NO_AIRPORT_MATCHING);
            }
            StringBuilder url = new StringBuilder(Constants.BASE_URL + Constants.FLIGHT_SCHEDULES);
            return findPossibleFlights(sourceAirportsCloserThanThreshold, departureAirportsCloserThanThreshold, url, tripInfo, entity, accommodationInfoDto);

        } catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

    }

    private TreeMap<Long, List<Flight>> findPossibleFlights(List<AirportInfoDto> startAirports, List<AirportInfoDto> destinationAirports, StringBuilder url, TripDataDto tripInfo, HttpEntity<?> entity,
                                                            AccommodationInfoDto accommodationInfoDto) {
        TreeMap<Long, List<Flight>> allPossibleFlights = new TreeMap<>();
        for (var sourceAirport : startAirports){
            for (var destinationAirport : destinationAirports) {
                String finalUrl = url.toString() + sourceAirport.airportCode() + "/" + destinationAirport.airportCode() + "/" + tripInfo.startDate();
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(finalUrl).queryParam("limit", Constants.FLIGHT_PROPOSAL_LIMIT);
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                            builder.build().toUri(),
                            HttpMethod.GET,
                            entity,
                            String.class);

                    var flightProposal = parseResponseToFlight(response.getBody(), tripInfo, accommodationInfoDto);
                    allPossibleFlights.put(flightProposal.getKey(), flightProposal.getValue());

                } catch (Exception ex) {
                    log.warn("Flight not found");
                }

            }
        }
        return allPossibleFlights;
    }

    private Map.Entry<Long, List<Flight>> parseResponseToFlight(String flightSchedule,
                                                                TripDataDto tripInfo, AccommodationInfoDto accommodationInfoDto) {
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

        var firstFlight = flightList.get(0);
        var lastFlight = flightList.get(flightList.size() - 1);

        firstFlight.setTravelToAirportDuration(findTravelDuration(firstFlight.getDepartureAirport(), tripInfo.startingLocation()));
        lastFlight.setTravelToAccommodationDuration(findTravelDuration(lastFlight.getArrivalAirport(), accommodationInfoDto.streetAddress()));
        totalDuration = totalDuration.plus(firstFlight.getTravelToAirportDuration());
        totalDuration = totalDuration.plus(lastFlight.getTravelToAccommodationDuration());



        return Map.entry(totalDuration.getSeconds(), flightList);
    }

    private Duration findTravelDuration(String airport, String location)  {
        DirectionsResult route;
        try {
            route = DirectionsApi.getDirections(context, location, "airport " + airport).await();
        } catch (ApiException | InterruptedException | IOException e) {
            return Duration.ZERO;
        }
        return Duration.ofSeconds(route.routes[0].legs[0].duration.inSeconds);
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
            map.add(Constants.CLIENT_ID , client_id);
            map.add(Constants.CLIENT_SECRET, client_secret);
            map.add(Constants.GRANT_TYPE, grant_type);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(Constants.BASE_URL + "/v1/oauth/token",
                                          HttpMethod.POST,
                                          entity,
                                          String.class);
            JSONObject json = new JSONObject(response.getBody());

            lufthansaKey.setLufthansaToken("Bearer " + json.getString(Constants.ACCESS_TOKEN));
            var expirationTime = json.getLong(Constants.EXPIRATION);
            this.tokenExpirationDate = Instant.now().plusSeconds(expirationTime);
            testLufthansaRequest();

        } catch (Exception ex) {
            throw new RuntimeException("Exception while getting Lufthansa access token");
        }
    }

    private boolean isTokenExpired() {
        return tokenExpirationDate == null || !Instant.now().isBefore(tokenExpirationDate);
    }

    private boolean isTokenExpiredEx() {

        if (lufthansaKey.getLufthansaToken() == null || !lufthansaKey.getLufthansaToken().startsWith("Bearer")) {
            return true;
        }
        return testLufthansaRequest();
    }

    private boolean testLufthansaRequest() {
        try {
            HttpEntity<?> entity = getHttpEntity();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String url = Constants.BASE_URL + Constants.COUNTRY_QUERY + Constants.DENMARK;
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("limit", Constants.COUNTRY_LIMIT);
            restTemplate.exchange(
                    builder.build().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);

            return false;
        }
        catch (Exception ex){
            return true;
        }
    }
}
