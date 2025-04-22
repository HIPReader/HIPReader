package com.example.hipreader.common.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.hipreader.domain.userbook.service.UserBookDocumentMigrationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StartupMigrationRunner implements ApplicationRunner {

	private final UserBookDocumentMigrationService migrationService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		migrationService.migrate();
	}
}
