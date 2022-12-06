package com.zpi.availabilityservice;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;

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

	private static final String dateFormat = "yyyy-MM-dd";
	private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> {
			builder.simpleDateFormat(dateTimeFormat);
			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
			builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
		};
	}

}
