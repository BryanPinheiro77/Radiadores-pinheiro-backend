package com.radiadorespinheiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RadiadoresPinheiroBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RadiadoresPinheiroBackendApplication.class, args);
	}

}
