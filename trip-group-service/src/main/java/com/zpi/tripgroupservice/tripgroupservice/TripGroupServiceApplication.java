package com.zpi.tripgroupservice.tripgroupservice;
import com.google.maps.GeoApiContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class TripGroupServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(TripGroupServiceApplication.class, args);
	}

	@Bean
	public GeoApiContext context() {
		return new GeoApiContext.Builder().apiKey("nope")
				.build();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
