package com.zpi.availabilityservice.it;

import com.zpi.availabilityservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AvailabilityUserServiceIntegrationTest {
    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL_APP_USER = "http://localhost:8081/api/v1/user/";

    @Test
    void shouldReturnCorrectListOfUsersInfo() {
        List<Long> userIdsList = List.of(1L, 2L);
        String suffix = "users";

        var result = callUserGroupGetEndpoints(userIdsList, suffix);

        var user = parseToListOfUsers(result.getBody());
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(user.username()).isEqualTo("DorBest");
        assertThat(user.firstName()).isEqualTo("Dorian");
        assertThat(user.lastName()).isEqualTo("Best");
    }

    private UserDto parseToListOfUsers(List body) {
        if (body.size() == 1) {
            LinkedHashMap<String, String> user = (LinkedHashMap<String, String>) body.get(0);
            var firstName = user.get("firstName");
            var lastName = user.get("lastName");
            var username = user.get("username");
            return new UserDto(1L, username, firstName, lastName);
        }
        return new UserDto();
    }

    private ResponseEntity<List> callUserGroupGetEndpoints(List<Long> userIdsList, String suffix) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "availability");
        HttpEntity<?> entity = new HttpEntity<>(userIdsList, headers);

        return restTemplate.exchange(
                BASE_URL_APP_USER + suffix,
                HttpMethod.POST,
                entity,
                List.class
        );
    }

}