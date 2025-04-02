package com.example.hipreader.domain.user.service;

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
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.hipreader.domain.user.dto.request.DeleteUserRequestDto;
import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.role.UserRole;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void getUserSuccess() {
		// Given
		Long userId = 1L;
		User mockUser = User.builder()
			.id(userId)
			.email("test@example.com")
			.build();
		when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

		// When
		GetUserResponseDto result = userService.getUser(userId);

		// Then
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getEmail()).isEqualTo("test@example.com");
	}

	@Test
	void exceptionForInvalidUser() {
		// Given
		Long invalidUserId = 999L;
		when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> userService.getUser(invalidUserId))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining(USER_NOT_FOUND.getMessage());
	}

	// 비밀번호 변경 테스트
	@Test
	void changePasswordSuccess() {
		// Given
		Long userId = 1L;
		ChangePasswordRequestDto request = new ChangePasswordRequestDto(
			"oldPass123!",
			"NewPass123!"
		);

		User mockUser = User.builder()
			.password("encodedOldPass")
			.build();

		when(userRepository.findUserById(userId)).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches("oldPass123!", "encodedOldPass")).thenReturn(true);
		when(passwordEncoder.matches("NewPass123!", "encodedOldPass")).thenReturn(false);
		when(passwordEncoder.encode("NewPass123!")).thenReturn("encodedNewPass");

		// When
		userService.changePassword(userId, request);

		// Then
		verify(passwordEncoder).encode("NewPass123!");
		assertThat(mockUser.getPassword()).isEqualTo("encodedNewPass");
	}

	@Test
	void changePasswordButNotInvalidPassword() {
		ChangePasswordRequestDto invalidRequest = new ChangePasswordRequestDto(
			"oldPass",
			"short"
		);

		assertThatThrownBy(() -> userService.changePassword(1L, invalidRequest))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining(INVALID_NEW_PASSWORD_FORMAT.getMessage());
	}

	// 사용자 삭제 테스트
	@Test
	void deleteUserSuccess() {
		// Given
		AuthUser authUser = new AuthUser(1L, "user@example.com", UserRole.ROLE_ADMIN);
		DeleteUserRequestDto request = new DeleteUserRequestDto("validPass");

		User mockUser = spy(User.builder()
			.password("encodedPass")
			.build());

		when(userRepository.findUserById(1L)).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches("validPass", "encodedPass")).thenReturn(true);

		// When
		userService.deleteUser(authUser, request);

		// Then
		verify(mockUser).deleteUser();
	}

	@Test
	void deleteUser_잘못된_비밀번호() {
		// Given
		AuthUser authUser = new AuthUser(1L, "user@example.com", UserRole.ROLE_ADMIN);
		DeleteUserRequestDto request = new DeleteUserRequestDto("wrongPass");

		User mockUser = User.builder()
			.password("encodedPass")
			.build();

		when(userRepository.findUserById(1L)).thenReturn(Optional.of(mockUser));
		when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> userService.deleteUser(authUser, request))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining(INVALID_PASSWORD.getMessage());
	}
}
