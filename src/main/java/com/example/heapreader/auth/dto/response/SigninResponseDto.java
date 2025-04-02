package com.example.heapreader.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SigninResponseDto {
	private final String token;
}
