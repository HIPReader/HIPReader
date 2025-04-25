package com.example.hipreader.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SigninResponseDto {
	private final String accessToken;
	private final String refreshToken;
	private final String nickname;
}
