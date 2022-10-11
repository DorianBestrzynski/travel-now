package com.zpi.transportservice.transportservice.transport;

import com.zpi.transportservice.transportservice.accommodation_transport.AccommodationTransportService;
import com.zpi.transportservice.transportservice.commons.TransportType;
import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import com.zpi.transportservice.transportservice.dto.AirportInfoDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.zpi.transportservice.transportservice.commons.Constants.*;

@Service
@RequiredArgsConstructor
public class TransportService {
    private final TransportRepository transportRepository;
    private final AccommodationTransportService accommodationTransportService;
    private final RestTemplate restTemplate;
    private String bearerAccessToken = "Bearer a4uam932aqcpeegu4zyu2azy";
    @Value("${client_id}")
    private String client_id;
    @Value("${client_secret}")
    private String client_secret;
    @Value("${grant_type}")
    private String grant_type;

    @Transactional
    public boolean generateTransportForAccommodation(AccommodationInfoDto accommodationInfoDto){
        var accommodationTransportIdList = accommodationTransportService.findAccommodationTransport(accommodationInfoDto.accommodationId())
                .stream()
                .map(accommodationTransport -> accommodationTransport.getId().getTransportId())
                .toList();;
        var res = generateTransportForAccommodationAir(accommodationInfoDto, accommodationTransportIdList);
        return true;
    }

    public boolean generateTransportForAccommodationAir(AccommodationInfoDto accommodationInfoDto,List<Long> accommodationTransportIds) {
        var shouldGenerateTransportAir = shouldGenerateTransportAir(accommodationInfoDto, accommodationTransportIds);
        if(shouldGenerateTransportAir){
            generateTransportAir(accommodationInfoDto);
        }
        return true;
    }

    private void generateTransportAir(AccommodationInfoDto accommodationInfoDto) {
        var nearestAirport = findNearestAirport(accommodationInfoDto);
        var flightProposals = findFlightProposals(nearestAirport);

    }

    private String findFlightProposals(List<AirportInfoDto> nearestAirport) {
        return "";
    }

    private List<AirportInfoDto> findNearestAirport(AccommodationInfoDto accommodationInfoDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization",bearerAccessToken);
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            // TODO get it from google maps
            double latitude = 39.52;
            double longitude = 2.73;
            var url = BASE_URL +NEAREST_AIRPORT + latitude + "," + longitude;

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class);

            System.out.println(response.getBody());


            JSONObject nearestAirportResource = new JSONObject(response.getBody());
            var airports = (JSONArray) nearestAirportResource.query("/NearestAirportResource/Airports/Airport");
            List<AirportInfoDto> airportInfo = new ArrayList<>();
            for(int x=0; x < airports.length(); x++){
                var airportCode =airports.getJSONObject(x).getString("AirportCode");
                var distance = (JSONObject) airports.getJSONObject(x).query("/Distance");
                var actualDistance = distance.getInt("Value");
                airportInfo.add(new AirportInfoDto(airportCode, actualDistance));

            }
            return airportInfo;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
//            generateAccessToken();
            throw new RuntimeException("Error while getting data from Lufthansa");
        }
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

    private boolean shouldGenerateTransportAir(AccommodationInfoDto accommodationInfoDto, List<Long> accomodationTransportIds) {
        var transportAir = transportRepository.findTransportAir(accomodationTransportIds, TransportType.AIRPLANE);
        if(transportAir.isEmpty()) {
            var matchingAccommodationTransportAir = transportRepository.findMatchingTransport(accommodationInfoDto.startingLocation(),
                    accommodationInfoDto.streetAddress(), accommodationInfoDto.startDate(), accommodationInfoDto.endDate(), TransportType.AIRPLANE);
            if(matchingAccommodationTransportAir.isEmpty()){
                return true;
            }
            accommodationTransportService.createAccommodationTransport(accommodationInfoDto.accommodationId(), matchingAccommodationTransportAir.get(0).getTransportId());
            return false;
        }
        return false;
    }
}
