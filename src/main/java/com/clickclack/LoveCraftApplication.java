package com.clickclack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@ComponentScan(basePackages = {"com.clickclack.controller", "com.clickclack.utils", "com.clickclack.service", "com.clickclack"})public class LoveCraftApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoveCraftApplication.class, args);
	}

}
