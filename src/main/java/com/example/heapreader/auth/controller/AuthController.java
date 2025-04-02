package com.example.heapreader.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.heapreader.auth.dto.request.RefreshTokenRequestDto;
import com.example.heapreader.auth.dto.request.SigninRequestDto;
import com.example.heapreader.auth.dto.request.SignupRequestDto;
import com.example.heapreader.auth.dto.response.SigninResponseDto;
import com.example.heapreader.auth.dto.response.SignupResponseDto;
import com.example.heapreader.auth.service.AuthService;
import com.example.heapreader.domain.refreshtoken.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	public ResponseEntity<SignupResponseDto> signup(
		@Valid SignupRequestDto signupRequestDto
	) {
		SignupResponseDto signupResponseDto = authService.signUp(signupRequestDto);

		String token = signupResponseDto.getToken();

		return ResponseEntity.ok().header("Authorization",token)
			.body(signupResponseDto);

	}

	@PostMapping("/signin")
	public ResponseEntity<SigninResponseDto> signIn(
		@Valid @RequestBody SigninRequestDto signinRequestDto
	) {
		SigninResponseDto signinResponseDto = authService.signIn(signinRequestDto);

		String bearerToken = signinResponseDto.getToken();

		return ResponseEntity.ok()
			.header("Authorization",bearerToken)
			.build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<String> refreshAccessToken(
		@RequestBody RefreshTokenRequestDto request) {

		String newAccessToken = refreshTokenService.refreshAccessToken(request.getRefreshToken());

		return ResponseEntity.ok(newAccessToken);
	}

}
