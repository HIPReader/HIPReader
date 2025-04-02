package com.example.hipreader.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.request.RefreshTokenRequestDto;
import com.example.hipreader.auth.dto.request.SigninRequestDto;
import com.example.hipreader.auth.dto.request.SignupRequestDto;
import com.example.hipreader.auth.dto.response.SigninResponseDto;
import com.example.hipreader.auth.dto.response.SignupResponseDto;
import com.example.hipreader.auth.service.AuthService;
import com.example.hipreader.domain.refreshtoken.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@RequestMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(
		@Valid SignupRequestDto signupRequestDto
	) {
		SignupResponseDto signupResponseDto = authService.signUp(signupRequestDto);

		String accessToken = signupResponseDto.getAccessToken();

		return ResponseEntity.ok().header("Authorization",accessToken)
			.body(signupResponseDto);

	}

	@PostMapping("/signin")
	public ResponseEntity<SigninResponseDto> signIn(
		@Valid @RequestBody SigninRequestDto signinRequestDto
	) {
		SigninResponseDto signinResponseDto = authService.signIn(signinRequestDto);

		String accessToken = signinResponseDto.getAccessToken();

		return ResponseEntity.ok()
			.header("Authorization",accessToken)
			.build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<String> refreshAccessToken(
		@RequestBody RefreshTokenRequestDto request) {

		String newAccessToken = refreshTokenService.refreshAccessToken(request.getRefreshToken());

		return ResponseEntity.ok(newAccessToken);
	}

}
