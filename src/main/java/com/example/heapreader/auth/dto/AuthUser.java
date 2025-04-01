package com.example.heapreader.auth.dto;

import java.util.List;

import com.example.heapreader.user.role.UserRole;

import lombok.Getter;

@Getter
public class AuthUser {

	private final Long id;
	private final String email;
	private final List<UserRole> authority;

	public AuthUser(List<UserRole> authority, String email, Long id) {
		this.authority = authority;
		this.email = email;
		this.id = id;
	}
}
