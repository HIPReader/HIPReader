package com.example.hipreader.auth.service;

import static com.example.hipreader.common.exception.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.hipreader.auth.dto.request.SigninRequestDto;
import com.example.hipreader.auth.dto.request.SignupRequestDto;
import com.example.hipreader.auth.dto.response.SigninResponseDto;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.ConflictException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.auth.repository.RefreshTokenRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.UserRole;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private AuthService authService;


	@Test
	void signUpDuplicatedEmail() {
		// Given
		SignupRequestDto request = new SignupRequestDto(
			"user123",
			"duplicate@example.com",
			"Password123!",
			25,
			"MALE",
			"ROLE_USER"
		);

		when(userRepository.existsUserByEmail(request.getEmail())).thenReturn(true);

		// When & Then
		assertThatThrownBy(() -> authService.signUp(request))
			.isInstanceOf(ConflictException.class)
			.hasMessageContaining(USER_EMAIL_DUPLICATION.getMessage());
	}

	// 로그인 테스트
	@Test
	void signInSuccess() {
		// Given
		SigninRequestDto request = new SigninRequestDto("test@example.com", "validPassword");
		User mockUser = User.builder()
			.id(1L)
			.email("test@example.com")
			.password("encodedPass")
			.role(UserRole.ROLE_USER)
			.nickname("user123")
			.build();

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);
		when(jwtUtil.createAccessToken(anyLong(), anyString(), any(), anyString())).thenReturn("newAccessToken");
		when(jwtUtil.createRefreshToken(anyLong())).thenReturn("newRefreshToken");

		// When
		SigninResponseDto response = authService.signIn(request);

		// Then
		verify(refreshTokenRepository).save(argThat(token ->
			token.getUserId().equals(1L) &&
				token.getRefreshToken().equals("newRefreshToken")
		));

		assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
		assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
	}

	@Test
	void signInButNotInvalidUser() {
		// Given
		SigninRequestDto request = new SigninRequestDto("wrong@example.com", "anyPass");
		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> authService.signIn(request))
			.isInstanceOf(NotFoundException.class)
			.hasMessageContaining(USER_NOT_FOUND.getMessage());
	}

	@Test
	void signInButInvalidPassword() {
		// Given
		SigninRequestDto request = new SigninRequestDto("test@example.com", "wrongPass");
		User mockUser = User.builder()
			.email("test@example.com")
			.password("encodedPass")
			.build();

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> authService.signIn(request))
			.isInstanceOf(BadRequestException.class)
			.hasMessageContaining(INVALID_PASSWORD.getMessage());
	}
}
