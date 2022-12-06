package com.zpi.availabilityservice;

import com.zpi.availabilityservice.availability.AvailabilityService;
import com.zpi.availabilityservice.dto.AvailabilityDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AvailabilityServiceApplication {

	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(AvailabilityServiceApplication.class, args);

//		var service = (AvailabilityService) applicationContext.getBean(AvailabilityService.class);
//		var dto = new AvailabilityDto(1L, 1L, LocalDate.now(), LocalDate.of(2022, 10, 1));
//		service.addNewAvailability(dto);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
