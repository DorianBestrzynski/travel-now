package com.zpi.financeoptimizerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FinanceOptimazerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceOptimazerServiceApplication.class, args);
	}

}
