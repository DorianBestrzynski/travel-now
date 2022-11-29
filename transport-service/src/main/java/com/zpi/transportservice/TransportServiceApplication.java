package com.zpi.transportservice;

import com.google.maps.GeoApiContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class TransportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransportServiceApplication.class, args);
	}

	@Bean
	public GeoApiContext context() {
		return new GeoApiContext.Builder().apiKey("nope")
										  .build();
	}

}
