package com.example.heapreader.common.util;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import com.example.heapreader.auth.dto.AuthUser;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private final AuthUser authUser;

	public JwtAuthenticationToken(AuthUser authUser) {
		super(authUser.getAuthority());
		this.authUser = authUser;
		setAuthenticated(false);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authUser;
	}
}
