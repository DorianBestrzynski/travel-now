package com.zpi.accommodationservice.accommodationservice;

import com.google.maps.GeoApiContext;
import com.zpi.accommodationservice.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zpi.accommodationservice.accommodationservice.comons.Utils.AIR_BNB_JSON_EXTRACTION_REGEX;
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.SERVICE_REGEX;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class AccommodationServiceApplication {

    public static void main(String[] args) throws IOException {
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
        return Pattern.compile(AIR_BNB_JSON_EXTRACTION_REGEX);
    }

    @Bean
    public Pattern serviceRegexPattern() {
        return Pattern.compile(SERVICE_REGEX);
    }

    @Bean
    public GeoApiContext context() {
        return new GeoApiContext.Builder().apiKey("AIzaSyAFtTPuDCYIQrU_KYFWS7cCMlOGNYHTKRU")
                                          .build();
    }
}
