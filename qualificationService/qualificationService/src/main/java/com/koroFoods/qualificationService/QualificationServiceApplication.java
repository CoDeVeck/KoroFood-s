package com.koroFoods.qualificationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QualificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QualificationServiceApplication.class, args);
	}

}
