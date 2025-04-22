package com.example.hipreader.common.filter;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.hipreader.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordValidator {

	private final PasswordEncoder passwordEncoder;

	public void validate(String newPassword, String oldPassword) {
		if (newPassword.length() < 8
			|| !newPassword.matches(".*\\d.*")
			|| !newPassword.matches(".*[A-Z].*")) {
			throw new BadRequestException(INVALID_NEW_PASSWORD_FORMAT);
		}

		if (passwordEncoder.matches(newPassword, oldPassword)) {
			throw new BadRequestException(PASSWORD_SAME_AS_OLD);
		}
	}

	public void validateOldPassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BadRequestException(INVALID_PASSWORD);
		}
	}

	public String encode(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
}
