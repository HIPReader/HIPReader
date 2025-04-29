package com.example.hipreader.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.request.ForgotPasswordRequestDto;
import com.example.hipreader.auth.dto.request.RefreshTokenRequestDto;
import com.example.hipreader.auth.dto.request.ResetPasswordRequestDto;
import com.example.hipreader.auth.dto.request.SigninRequestDto;
import com.example.hipreader.auth.dto.request.SignupRequestDto;
import com.example.hipreader.auth.dto.response.SigninResponseDto;
import com.example.hipreader.auth.dto.response.SignupResponseDto;
import com.example.hipreader.auth.service.AuthService;
import com.example.hipreader.auth.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final RefreshTokenService refreshTokenService;

	@PostMapping("/v1/auth/signup")
	public ResponseEntity<SignupResponseDto> signup(
		@Valid @RequestBody SignupRequestDto signupRequestDto
	) {
		SignupResponseDto signupResponseDto = authService.signUp(signupRequestDto);

		String accessToken = signupResponseDto.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);

		return new ResponseEntity<>(signupResponseDto, headers, HttpStatus.CREATED);

	}

	@PostMapping("/v1/auth/signin")
	public ResponseEntity<SigninResponseDto> signIn(
		@Valid @RequestBody SigninRequestDto signinRequestDto
	) {
		SigninResponseDto signinResponseDto = authService.signIn(signinRequestDto);

		String accessToken = signinResponseDto.getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", accessToken);

		return new ResponseEntity<>(signinResponseDto, headers, HttpStatus.OK);
	}

	@PostMapping("/v1/auth/refresh")
	public ResponseEntity<String> refreshAccessToken(
		@RequestBody RefreshTokenRequestDto request) {

		String newAccessToken = refreshTokenService.refreshAccessToken(request.getRefreshToken());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", newAccessToken);

		return new ResponseEntity<>(headers, HttpStatus.CREATED);
	}

	@PostMapping("/v1/auth/forgot-password")
	public ResponseEntity<Void> forgotPassword(
		@RequestBody @Valid ForgotPasswordRequestDto forgotPasswordRequestDto) {
		authService.requestPasswordReset(forgotPasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/v1/auth/reset-password")
	public ResponseEntity<Void> resetPassword(
		@RequestBody @Valid ResetPasswordRequestDto resetPasswordRequestDto
	) {
		authService.resetPassword(resetPasswordRequestDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
