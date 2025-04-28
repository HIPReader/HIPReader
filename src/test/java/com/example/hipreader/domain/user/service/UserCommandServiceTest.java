package com.example.hipreader.domain.user.service;

import static com.example.hipreader.common.exception.ErrorCode.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.filter.PasswordValidator;
import com.example.hipreader.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.hipreader.domain.user.dto.request.DeleteUserRequestDto;
import com.example.hipreader.domain.user.dto.request.UpdateUserRequestDto;
import com.example.hipreader.domain.user.dto.response.UpdateUserResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordValidator passwordValidator;

	@InjectMocks
	private UserCommandService userCommandService;

	@Test
	void testUpdateUserSuccess() {
		// Given
		AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.ROLE_ADMIN);
		UpdateUserRequestDto request = new UpdateUserRequestDto(
			"newNickname", 30, Gender.FEMALE
		);

		User user = User.builder()
			.id(1L)
			.nickname("oldNickname")
			.age(25)
			.gender(Gender.MALE)
			.build();

		when(userRepository.findActiveUserById(authUser.getId())).thenReturn(Optional.of(user));

		// When
		UpdateUserResponseDto response = userCommandService.updateUser(authUser, request);

		// Then
		assertEquals("newNickname", response.getNickname());
		assertEquals(30, response.getAge());
		assertEquals(Gender.FEMALE, response.getGender());
		verify(userRepository).findActiveUserById(authUser.getId());
	}

	@Test
	void testChangePasswordSuccess() {
		// Given
		AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.ROLE_ADMIN);
		ChangePasswordRequestDto request = new ChangePasswordRequestDto("oldPass123!", "NewPass123!");
		User user = User.builder()
			.id(1L)
			.password("encodedOldPassword")
			.build();

		when(userRepository.findActiveUserById(authUser.getId()))
			.thenReturn(Optional.of(user));

		when(passwordValidator.encode("NewPass123!")).thenReturn("encodedNewPassword");

		// When
		userCommandService.changePassword(authUser, request);

		// Then
		assertEquals("encodedNewPassword", user.getPassword());
	}

	@Test
	void testChangePasswordInvalidOldPassword() {
		// Given
		AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.ROLE_ADMIN);
		ChangePasswordRequestDto request = new ChangePasswordRequestDto(
			"wrongOldPass", "NewPass123!"
		);

		User user = User.builder()
			.id(1L)
			.password("encodedOldPassword")
			.build();

		when(userRepository.findActiveUserById(authUser.getId())).thenReturn(Optional.of(user));
		doThrow(new BadRequestException(INVALID_PASSWORD))
			.when(passwordValidator).validateOldPassword(request.getOldPassword(), user.getPassword());

		// When & Then
		assertThrows(BadRequestException.class,
			() -> userCommandService.changePassword(authUser, request));
	}

	@Test
	void testDeleteUser_Success() {
		// Given
		AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.ROLE_USER);
		DeleteUserRequestDto request = new DeleteUserRequestDto("correctPassword");

		User mockUser = mock(User.class);
		when(mockUser.getPassword()).thenReturn("encodedPassword");

		when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(mockUser));
		doNothing().when(passwordValidator).validateOldPassword("correctPassword", "encodedPassword");

		// When
		userCommandService.deleteUser(authUser, request);

		// Then
		verify(mockUser).deleteUser(); // 삭제 메서드 호출 검증
		verify(userRepository).findActiveUserById(1L);
	}

	@Test
	void testDeleteUser_InvalidPassword() {
		// Given
		AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.ROLE_USER);
		DeleteUserRequestDto request = new DeleteUserRequestDto("wrongPassword");

		User mockUser = mock(User.class);
		when(mockUser.getPassword()).thenReturn("encodedPassword");

		when(userRepository.findActiveUserById(1L)).thenReturn(Optional.of(mockUser));
		doThrow(new BadRequestException(INVALID_PASSWORD))
			.when(passwordValidator).validateOldPassword("wrongPassword", "encodedPassword");

		// When & Then
		assertThrows(BadRequestException.class,
			() -> userCommandService.deleteUser(authUser, request));
		verify(mockUser, never()).deleteUser(); // 삭제 메서드 미호출 검증
	}

	@Test
	void testUpdateUser_UserNotFound() {
		// Given
		AuthUser authUser = new AuthUser(999L, "ghost@example.com", UserRole.ROLE_ADMIN);
		UpdateUserRequestDto request = new UpdateUserRequestDto("닉네임", 20, Gender.FEMALE);
		when(userRepository.findActiveUserById(authUser.getId())).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundException.class,
			() -> userCommandService.updateUser(authUser, request));
	}

}
