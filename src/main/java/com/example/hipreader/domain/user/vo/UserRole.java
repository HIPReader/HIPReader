package com.example.hipreader.domain.user.vo;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.NotFoundException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole implements GrantedAuthority {

	ROLE_USER(Authority.USER),
	ROLE_ADMIN(Authority.ADMIN);

	private final String userRole;

	public static UserRole of(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new NotFoundException(INVALID_USER_ROLE));
	}

	@Override
	public String getAuthority() {
		return name();
	}

	public static class Authority {
		public static final String USER = "ROLE_USER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}
