package com.example.heapreader.user.role;


import java.util.Arrays;

import org.springframework.web.server.ResponseStatusException;

import com.example.heapreader.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserRole {

	ROLE_USER(Authority.USER),
	ROLE_ADMIN(Authority.ADMIN);

	private final String userRole;

	public static UserRole of(String userRole) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(userRole))
			.findFirst()
			.orElseThrow(() -> new ResponseStatusException(ErrorCode.INVALID_USER_ROLE.getStatus()));
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
