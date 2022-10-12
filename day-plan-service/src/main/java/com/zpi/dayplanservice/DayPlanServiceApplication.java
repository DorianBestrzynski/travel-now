package com.zpi.dayplanservice;

import com.google.maps.GeoApiContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class DayPlanServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DayPlanServiceApplication.class, args);
	}

	@Bean
	public GeoApiContext context() {
		return new GeoApiContext.Builder().apiKey("")
										  .build();
	}

}
