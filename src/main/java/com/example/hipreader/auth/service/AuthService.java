package com.example.hipreader.auth.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.auth.dto.request.SigninRequestDto;
import com.example.hipreader.auth.dto.request.SignupRequestDto;
import com.example.hipreader.auth.dto.response.SigninResponseDto;
import com.example.hipreader.auth.dto.response.SignupResponseDto;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.domain.refreshtoken.entity.RefreshToken;
import com.example.hipreader.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.gender.Gender;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.role.UserRole;

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

		UserRole userRole = UserRole.of(signupRequestDto.getRole());
		Gender gender = Gender.valueOf(signupRequestDto.getGender());

		User newUser = User.builder()
			.nickname(signupRequestDto.getNickname())
			.email(signupRequestDto.getEmail())
			.password(encodedPassword)
			.role(userRole)
			.age(signupRequestDto.getAge())
			.gender(gender)
			.build();

		User savedUser = userRepository.save(newUser);

		String accessToken = jwtUtil.createAccessToken(savedUser.getId(),savedUser.getEmail(),userRole,savedUser.getNickname());

		String refreshToken = jwtUtil.createRefreshToken(savedUser.getId());

		return new SignupResponseDto(accessToken,refreshToken);
	}

	@Transactional(readOnly = true)
	public SigninResponseDto signIn(@Valid SigninRequestDto signinRequestDto) {
		User user = userRepository.findByEmail(signinRequestDto.getEmail()).orElseThrow(
			() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(),USER_NOT_FOUND.getMessage()));

		if (!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
		}

		String accessToken = jwtUtil.createAccessToken(user.getId(),user.getEmail(),user.getRole(),user.getNickname());

		String refreshToken = jwtUtil.createRefreshToken(user.getId());

		refreshTokenRepository.save(new RefreshToken(user.getId(),refreshToken));

		return new SigninResponseDto(accessToken,refreshToken);
	}


}
