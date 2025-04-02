package com.example.hipreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HeapreaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeapreaderApplication.class, args);
	}

}
