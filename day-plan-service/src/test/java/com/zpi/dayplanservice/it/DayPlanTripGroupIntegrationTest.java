package com.zpi.dayplanservice.it;

import com.zpi.dayplanservice.dto.AccommodationInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
public class DayPlanTripGroupIntegrationTest {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL_USER_GROUP = "http://localhost:8082/api/v1/user-group/";

    private static final String BASE_URL_TRIP_GROUP = "http://localhost:8082/api/v1/trip-group/";

    @Test
    void shouldReturnTrueWhenUserIsCoordinator() {
        Long groupId = 1L;
        Long userId = 1L;
        String suffix = "role";

        ResponseEntity<Boolean> response = callUserGroupGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(res.getBody()).isTrue();
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                }
        );
    }

    @Test
    void shouldReturnFalseWhenUserIsParticipant() {
        Long groupId = 1L;
        Long userId = 2L;
        String suffix = "role";

        ResponseEntity<Boolean> response = callUserGroupGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(res.getBody()).isFalse();
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                }
        );
    }


    @Test
    void shouldReturnFalseWhenUserIsNotPartOfAGroup() {
        Long groupId = 1L;
        Long userId = 3L;
        String suffix = "role";

        ResponseEntity<Boolean> response = callUserGroupGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(res.getBody()).isFalse();
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                }
        );
    }


    @Test
    void shouldReturnTrueWhenUserIsPartOfGroup() {
        Long groupId = 1L;
        Long userId = 1L;
        String suffix = "group";

        ResponseEntity<Boolean> response = callUserGroupGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(res.getBody()).isTrue();
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                }
        );
    }

    @Test
    void shouldReturnFalseWhenUserIsNotPartOfGroup() {
        Long groupId = 1L;
        Long userId = 12L;
        String suffix = "group";

        ResponseEntity<Boolean> response = callUserGroupGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(res.getBody()).isFalse();
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                }
        );
    }

    @Test
    void shouldReturnErrorWhenGroupIdOrUserIdInvalid() {
        Long groupId = -2L;
        Long userId = 3L;
        String suffix = "group";

        var ex = assertThrows(HttpClientErrorException.BadRequest.class,
                () ->  callUserGroupGetEndpoints(groupId, userId, suffix));

        assertThat(ex.getStatusCode().is4xxClientError()).isTrue();
        assertThat(ex.getStatusCode().value()).isEqualTo(400);
        assertThat(ex.getStatusCode().name()).isEqualTo("BAD_REQUEST");
    }

    @Test
    void shouldRReturnSelectedAccommodation() {
        Long groupId = 5L;
        String suffix = "accommodation";

        var response = callTripGroupGetEndpoints(groupId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                    assertThat(res.getBody()).satisfies(
                            ac -> {
                                assertThat(ac.groupId()).isEqualTo(5L);
                                assertThat(ac.city()).isEqualTo("Wrocław");
                            }
                    );
                }
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L})
    @NullSource
    void shouldThrowErrorOnIllegalParams(Long groupId) {
        String suffix = "accommodation";

        var ex = assertThrows(HttpClientErrorException.BadRequest.class,
                              () ->  callTripGroupGetEndpoints(groupId, suffix));

        assertThat(ex.getStatusCode().is4xxClientError()).isTrue();
        assertThat(ex.getStatusCode().value()).isEqualTo(400);
        assertThat(ex.getStatusCode().name()).isEqualTo("BAD_REQUEST");
    }

    @Test
    void shouldThrowNotFound() {
        Long groupId = 100L;
        String suffix = "accommodation";;

        var ex = assertThrows(HttpClientErrorException.NotFound.class,
                              () ->  callTripGroupGetEndpoints(groupId, suffix));

        assertThat(ex.getStatusCode().is4xxClientError()).isTrue();
        assertThat(ex.getStatusCode().value()).isEqualTo(404);
        assertThat(ex.getStatusCode().name()).isEqualTo("NOT_FOUND");
    }

    @Test
    void shouldReturnNullWhenGroupDidNotPickAccommodation() {
        Long groupId = 1L;
        String suffix = "accommodation";;

        var response = callTripGroupGetEndpoints(groupId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                    assertThat(res.getBody()).satisfies(
                            ac -> {
                                assertThat(ac.groupId()).isEqualTo(null);
                                assertThat(ac.city()).isEqualTo(null);
                            }
                    );
                }
        );
    }

    private ResponseEntity<AccommodationInfoDto> callTripGroupGetEndpoints(Long groupId, String suffix) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "dayplan");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL_TRIP_GROUP + suffix)
                                                 .queryParam("groupId", groupId)
                                                 .encode()
                                                 .toUriString();

        return restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                AccommodationInfoDto.class
        );
    }

    private ResponseEntity<Boolean> callUserGroupGetEndpoints(Long groupId, Long userId, String suffix) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "dayPlan");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL_USER_GROUP + suffix)
                .queryParam("groupId", groupId)
                .queryParam("userId", userId)
                .encode()
                .toUriString();

        return restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                Boolean.class
        );
    }

}
