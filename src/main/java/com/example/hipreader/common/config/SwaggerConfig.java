package com.example.hipreader.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		// Security Scheme 정의
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		// Security Requirement 정의
		SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

		return new OpenAPI()
			.info(new Info().title("Hipreader 독서 커뮤니티 플랫폼을 구현해보는 프로젝트입니다.")
				.description("API 명세서")
				.version("v1.0"))
			.addSecurityItem(securityRequirement) // Security Requirement 추가
			.schemaRequirement("BearerAuth", securityScheme); // Security Scheme 추가
	}
}
