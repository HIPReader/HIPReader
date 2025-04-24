package com.example.hipreader.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserQueryService userQueryService;

	@Test
	void testGetUserSuccess() {
		// Given
		Long userId = 1L;
		User mockUser = User.builder()
			.id(userId)
			.email("test@example.com")
			.nickname("tester")
			.build();

		when(userRepository.findActiveUserById(userId)).thenReturn(Optional.of(mockUser));

		// When
		GetUserResponseDto result = userQueryService.getUser(userId);

		// Then
		assertEquals(mockUser.getEmail(), result.getEmail());
		assertEquals(mockUser.getNickname(), result.getNickname());
		verify(userRepository).findActiveUserById(userId);
	}

	@Test
	void testGetUserNotFound() {
		// Given
		Long userId = 999L;
		when(userRepository.findActiveUserById(userId)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundException.class, () -> userQueryService.getUser(userId));
		verify(userRepository).findActiveUserById(userId);
	}

	@Test
	void testGetAllUsersSuccess() {
		// Given
		int page = 0;
		int size = 10;
		List<User> userList = List.of(
			User.builder().id(1L).email("user1@test.com").nickname("user1").build(),
			User.builder().id(2L).email("user2@test.com").nickname("user2").build()
		);

		Page<User> userPage = new PageImpl<>(
			userList,
			PageRequest.of(page, size),
			userList.size()
		);

		when(userRepository.findAllActiveUsers(any(PageRequest.class))).thenReturn(userPage);

		// When
		Page<GetUserResponseDto> result = userQueryService.getAllUsers(page, size);

		// Then
		assertEquals(2, result.getContent().size());

		// Verify first user
		GetUserResponseDto dto1 = result.getContent().get(0);
		assertEquals("user1@test.com", dto1.getEmail());
		assertEquals("user1", dto1.getNickname());

		// Verify pagination parameters
		ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
		verify(userRepository).findAllActiveUsers(captor.capture());

		PageRequest captured = captor.getValue();
		assertEquals(0, captured.getPageNumber());
		assertEquals(10, captured.getPageSize());
	}
}
