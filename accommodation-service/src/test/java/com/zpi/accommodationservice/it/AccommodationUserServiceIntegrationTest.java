package com.zpi.accommodationservice.it;

import com.zpi.accommodationservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccommodationUserServiceIntegrationTest {
    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL_APP_USER = "http://localhost:8081/api/v1/user/";

    @Test
    void shouldReturnCorrectListOfUsersInfo() {
        List<Long> userIdsList = List.of(1L, 8L);
        String suffix = "users";

        var result = callUserGroupGetEndpoints(userIdsList, suffix);

        var user = parseToListOfUsers(result.getBody());
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(user.phoneNumber()).isEqualTo("DorBest");
        assertThat(user.firstName()).isEqualTo("Dorian");
        assertThat(user.lastName()).isEqualTo("Best");
    }

    private UserDto parseToListOfUsers(List body) {
        if (body.size() == 1) {
            LinkedHashMap<String, String> user = (LinkedHashMap<String, String>) body.get(0);
            var email = user.get("email");
            var firstName = user.get("firstName");
            var lastName = user.get("lastName");
            var username = user.get("phoneNumber");
            return new UserDto(1L, username, email, firstName, lastName);
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
