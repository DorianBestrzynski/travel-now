package com.zpi.accommodationservice.it;

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
public class AccommodationTripGroupIntegrationTest {

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

    private ResponseEntity<Boolean> callUserGroupGetEndpoints(Long groupId, Long userId, String suffix) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "availability");
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
