package com.example.heapreader.auth.service;

import static com.example.heapreader.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.heapreader.auth.dto.request.SigninRequestDto;
import com.example.heapreader.auth.dto.request.SignupRequestDto;
import com.example.heapreader.auth.dto.response.SigninResponseDto;
import com.example.heapreader.auth.dto.response.SignupResponseDto;
import com.example.heapreader.common.util.JwtUtil;
import com.example.heapreader.domain.refreshtoken.entity.RefreshToken;
import com.example.heapreader.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.heapreader.domain.user.entity.User;
import com.example.heapreader.domain.user.gender.Gender;
import com.example.heapreader.domain.user.repository.UserRepository;
import com.example.heapreader.domain.user.role.UserRole;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public SignupResponseDto signUp(
		@Valid SignupRequestDto signupRequestDto) {

		if (userRepository.existsUserByEmail(signupRequestDto.getEmail())) {
			throw new ResponseStatusException(USER_EMAIL_DUPLICATION.getStatus(), USER_EMAIL_DUPLICATION.getMessage());
		}

		String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

		UserRole userRole = UserRole.of(signupRequestDto.getUserRole());
		Gender gender = Gender.valueOf(signupRequestDto.getGender());

		User newUser = User.builder()
			.nickname(signupRequestDto.getNickname())
			.email(signupRequestDto.getEmail())
			.password(encodedPassword)
			.userRole(userRole)
			.age(signupRequestDto.getAge())
			.gender(gender)
			.build();

		User savedUser = userRepository.save(newUser);

		String accessToken = jwtUtil.createAccessToken(savedUser.getId(),savedUser.getEmail(),userRole,savedUser.getNickname());

		return new SignupResponseDto(accessToken);
	}

	@Transactional(readOnly = true)
	public SigninResponseDto signIn(@Valid SigninRequestDto signinRequestDto) {
		User user = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
			() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));

		if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
		}

		String accessToken = jwtUtil.createAccessToken(user.getId(),user.getEmail(),user.getUserRole(),user.getNickname());

		String refreshToken = jwtUtil.createRefreshToken(user.getId(),accessToken);

		return new SigninResponseDto(accessToken);
	}

	public String refreshAccessToken(String refreshTokenValue) {
		// Refresh Token 조회 및 검증
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
			.orElseThrow(() -> new ResponseStatusException(TOKEN_NOT_FOUND.getStatus(), TOKEN_NOT_FOUND.getMessage()));

		if (!jwtUtil.validateRefreshToken(refreshToken.getRefreshToken())) {
			throw new ResponseStatusException(EXPIRED_TOKEN.getStatus(), EXPIRED_TOKEN.getMessage());
		}

		// 사용자 정보 조회
		Long userId = refreshToken.getId();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));

		// 새로운 Access Token 생성
		String newAccessToken = jwtUtil.createAccessToken(
			user.getId(), user.getEmail(), user.getUserRole(),user.getNickname());

		// Refresh Token 업데이트
		refreshTokenRepository.save(new RefreshToken(userId, newAccessToken));

		return newAccessToken;
	}
}
