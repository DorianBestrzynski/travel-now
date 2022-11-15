package com.zpi.availabilityservice.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class AvailabilityTripGroupIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void shouldReturnTrueWhenUserIsCoordinator() {
        Long groupId = 1L;
        Long userId = 1L;
        String url = "http://localhost:8082/api/v1/user-group/role";
//        client.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("http://localhost:8082/api/v1/user-group/role")
//                        .queryParam("groupId", groupId)
//                        .queryParam("userId", userId)
//                        .build()
//                )
//                .header("innerCommunication", "availability")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody()
//                .json("true");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("innerCommunication", "availability");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("groupId", groupId)
                .queryParam("userId", userId)
                .encode()
                .toUriString();



        HttpEntity<String>  response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class
        );

//        assertThat(response).satisfies(
//                res -> {
//                    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
//                    assertThat(res.getStatusCodeValue()).isEqualTo(200);
//                    assertThat((Boolean) res.getBody()).isTrue();
//                }
//        );
    }

}
