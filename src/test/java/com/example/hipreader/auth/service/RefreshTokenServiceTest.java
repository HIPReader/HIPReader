package com.example.hipreader.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hipreader.auth.entity.RefreshToken;
import com.example.hipreader.auth.repository.RefreshTokenRepository;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.UserRole;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private RefreshTokenService refreshTokenService;

	@Test
	void testRefreshAccessToken_InvalidToken() {
		// Given
		String invalidToken = "invalid.refresh.token";
		when(jwtUtil.validateRefreshToken(invalidToken)).thenReturn(false);

		// When & Then
		assertThrows(UnauthorizedException.class,
			() -> refreshTokenService.refreshAccessToken(invalidToken));
	}

	@Test
	void testRefreshAccessToken_TokenNotFoundInRedis() {
		// Given
		String validToken = "valid.refresh.token";
		when(jwtUtil.validateRefreshToken(validToken)).thenReturn(true);
		when(refreshTokenRepository.findById(validToken)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UnauthorizedException.class,
			() -> refreshTokenService.refreshAccessToken(validToken));
	}

	@Test
	void testRefreshAccessToken_UserNotFound() {
		// Given
		String validToken = "valid.refresh.token";
		RefreshToken storedToken = new RefreshToken(validToken, 1L);

		when(jwtUtil.validateRefreshToken(validToken)).thenReturn(true);
		when(refreshTokenRepository.findById(validToken)).thenReturn(Optional.of(storedToken));
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundException.class,
			() -> refreshTokenService.refreshAccessToken(validToken));
	}

	@Test
	void testRefreshAccessToken_Success() {
		// Given
		String validToken = "valid.refresh.token";
		Long userId = 1L;
		User mockUser = User.builder()
			.id(userId)
			.email("user@example.com")
			.role(UserRole.ROLE_USER)
			.nickname("user123")
			.build();

		RefreshToken storedToken = new RefreshToken(validToken, userId);

		when(jwtUtil.validateRefreshToken(validToken)).thenReturn(true);
		when(refreshTokenRepository.findById(validToken)).thenReturn(Optional.of(storedToken));
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
		when(jwtUtil.createAccessToken(anyLong(), anyString(), any(), anyString()))
			.thenReturn("new.access.token");

		// When
		String newAccessToken = refreshTokenService.refreshAccessToken(validToken);

		// Then
		verify(refreshTokenRepository).deleteById(validToken);
		assertEquals("new.access.token", newAccessToken);

		// Verify JWT creation parameters
		ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<UserRole> roleCaptor = ArgumentCaptor.forClass(UserRole.class);
		ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);

		verify(jwtUtil).createAccessToken(
			userIdCaptor.capture(),
			emailCaptor.capture(),
			roleCaptor.capture(),
			nicknameCaptor.capture()
		);

		assertEquals(userId, userIdCaptor.getValue());
		assertEquals("user@example.com", emailCaptor.getValue());
		assertEquals(UserRole.ROLE_USER, roleCaptor.getValue());
		assertEquals("user123", nicknameCaptor.getValue());
	}
}
