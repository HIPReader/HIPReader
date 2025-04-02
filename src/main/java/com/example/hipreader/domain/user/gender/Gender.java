package com.example.hipreader.domain.user.gender;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

	MALE("Male"),
	FEMALE("Female");

	private final String gender;

	@Override
	public String toString() {
		return gender;
	}
}
