package com.gestionvehiculos.vehiculos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VehiculosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehiculosApiApplication.class, args);
	}

}
