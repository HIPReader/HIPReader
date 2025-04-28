package com.example.hipreader.domain.user.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

	private final UserRepository userRepository;

	public GetUserResponseDto getUser(Long userId) {
		User user = userRepository.findActiveUserById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		return GetUserResponseDto.toDto(user);
	}

	public Page<GetUserResponseDto> getAllUsers(int page, int size) {
		Page<User> users = userRepository.findAllActiveUsers(PageRequest.of(page, size));
		return users.map(GetUserResponseDto::toDto);
	}
}
