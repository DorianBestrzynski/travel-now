package com.zpi.transportservice.it;

import com.zpi.transportservice.dto.AccommodationInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TransportAccommodationIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL_ACCOMMODATION_SERVICE = "http://localhost:8088/api/v1/accommodation/";

    @Test
    void shouldReturnAccommodationInfo() {
        Long accommodationId = 1L;
        String suffix = "info";

        ResponseEntity<AccommodationInfoDto> response = callAccommodationGetEndpoints(accommodationId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                    assertThat(Objects.requireNonNull(res.getBody()).groupId()).isEqualTo(1L);
                }
        );
    }

    @Test
    void shouldReturnErrorWhenAccommodationIdInvalid() {
        Long accommodationId = 100L;
        String suffix = "info";

        var ex = assertThrows(HttpClientErrorException.NotFound.class,
                () ->  callAccommodationGetEndpoints(accommodationId, suffix));

        assertThat(ex.getStatusCode().is4xxClientError()).isTrue();
        assertThat(ex.getStatusCode().value()).isEqualTo(404);
        assertThat(ex.getStatusCode().name()).isEqualTo("NOT_FOUND");
    }

    private ResponseEntity<AccommodationInfoDto> callAccommodationGetEndpoints(Long accommodationId, String suffix) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "accommodation");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL_ACCOMMODATION_SERVICE + suffix)
                .queryParam("accommodationId", accommodationId)
                .encode()
                .toUriString();

        return restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                AccommodationInfoDto.class
        );
    }


}
