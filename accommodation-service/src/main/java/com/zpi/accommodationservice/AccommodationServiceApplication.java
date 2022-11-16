package com.zpi.accommodationservice;

import com.google.maps.GeoApiContext;
import com.zpi.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import com.zpi.accommodationservice.comons.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class AccommodationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccommodationServiceApplication.class, args);
    }

    @Bean
    public HashMap<String, AccommodationDataExtractionStrategy> extractionStrategies(
            List<AccommodationDataExtractionStrategy> strategiesList) {

        return strategiesList.stream()
							 .collect(Collectors.toMap(AccommodationDataExtractionStrategy::getServiceName,
                                                      Function.identity(), (a, b) -> a,
                                                      HashMap::new));
    }

    @Bean
    public Pattern airbnbRegexPattern() {
        return Pattern.compile(Utils.AIR_BNB_JSON_EXTRACTION_REGEX);
    }

    @Bean
    public Pattern serviceRegexPattern() {
        return Pattern.compile(Utils.SERVICE_REGEX);
    }

    @Bean
    public GeoApiContext context() {
        return new GeoApiContext.Builder().apiKey("AIzaSyB8Gi9RYWrfzOJo4lANuh-VjTX70EkKwl4")
                                          .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
