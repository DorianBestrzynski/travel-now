package com.zpi.tripgroupservice.it;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TripGroupFinanceIntegrationTests {
    @Autowired
    private RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8086/api/v1/finance-request/";

    @Test
    void shouldReturnTrue() {
        Long groupId = 1L;
        Long userId = 1L;
        String suffix = "user";

        ResponseEntity<Boolean> response = callFinanceGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                    assertTrue(res.getBody());
                }
        );
    }

    @Test
    void shouldReturnFalse() {
        Long groupId = 2L;
        Long userId = 1L;
        String suffix = "user";

        ResponseEntity<Boolean> response = callFinanceGetEndpoints(groupId, userId, suffix);

        assertThat(response).satisfies(
                res -> {
                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
                    assertThat(Objects.requireNonNull(res.getHeaders().get("Content-Type")).get(0)).isEqualTo("application/json");
                    assertFalse(res.getBody());
                }
        );
    }

    @ParameterizedTest
    @MethodSource("nullParams")
    void shouldReturnErrorWhenParamsAreInvalid(Long groupId, Long userId) {
        String suffix = "user";

        var ex = assertThrows(HttpClientErrorException.BadRequest.class,
                              () ->  callFinanceGetEndpoints(groupId, userId , suffix));

        assertThat(ex.getStatusCode().is4xxClientError()).isTrue();
        assertThat(ex.getStatusCode().value()).isEqualTo(400);
        assertThat(ex.getStatusCode().name()).isEqualTo("BAD_REQUEST");
    }

    private static Stream<Arguments> nullParams() {
        return Stream.of(
                Arguments.arguments(null, 1L),
                Arguments.arguments(1L, null)
        );
    }

    private ResponseEntity<Boolean> callFinanceGetEndpoints(Long groupId, Long userId, String suffix) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "trip-group");
        headers.set("userId", String.valueOf(1L));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL + suffix)
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
