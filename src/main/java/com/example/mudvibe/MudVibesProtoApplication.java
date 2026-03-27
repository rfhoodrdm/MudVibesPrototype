package com.example.mudvibe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MudVibesProtoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MudVibesProtoApplication.class, args);
	}

}
