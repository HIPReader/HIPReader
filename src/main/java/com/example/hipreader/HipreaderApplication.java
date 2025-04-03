package com.example.hipreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaAuditing
public class HipreaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(HipreaderApplication.class, args);
	}

}
