package com.zpi.accommodationservice.accommodationservice;

import com.zpi.accommodationservice.accommodationservice.accomodation_strategy.AccommodationDataExtractionStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableFeignClients
public class AccommodationServiceApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(AccommodationServiceApplication.class, args);
	}

	@Bean
	public HashMap<String, AccommodationDataExtractionStrategy> extractionStrategies(
			List<AccommodationDataExtractionStrategy> strategiesList) {

		return strategiesList.stream().collect(
				Collectors.toMap(AccommodationDataExtractionStrategy::getServiceName, Function.identity(), (a, b) -> a,
						HashMap::new));
	}

}
