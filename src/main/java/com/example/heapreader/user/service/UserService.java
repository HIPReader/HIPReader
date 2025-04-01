package com.example.heapreader.user.service;

import static com.example.heapreader.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.heapreader.auth.dto.AuthUser;
import com.example.heapreader.user.dto.request.ChangePasswordRequestDto;
import com.example.heapreader.user.dto.request.DeleteUserRequestDto;
import com.example.heapreader.user.dto.response.GetUserResponseDto;
import com.example.heapreader.user.entity.User;
import com.example.heapreader.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public GetUserResponseDto getUser(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));
		return new GetUserResponseDto(user.getId(),user.getEmail());
	}

	@Transactional
	public void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto) {
		validateNewPassword(changePasswordRequestDto);

		User user = userRepository.findUserById(userId)
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

		if (!passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
			throw new ResponseStatusException(INVALID_PASSWORD.getStatus(), INVALID_PASSWORD.getMessage());
		}
		if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), user.getPassword())) {
			throw new ResponseStatusException(PASSWORD_SAME_AS_OLD.getStatus(), PASSWORD_SAME_AS_OLD.getMessage());
		}

		user.changePassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
	}

	private static void validateNewPassword(ChangePasswordRequestDto changePasswordRequestDto) {
		if (changePasswordRequestDto.getNewPassword().length() < 8 ||
			!changePasswordRequestDto.getNewPassword().matches(".*\\d.*") ||
			!changePasswordRequestDto.getNewPassword().matches(".*[A-Z].*")) {
			throw new ResponseStatusException(INVALID_NEW_PASSWORD_FORMAT.getStatus(), INVALID_NEW_PASSWORD_FORMAT.getMessage());
		}
	}

	@Transactional
	public void deleteUser(AuthUser authUser, DeleteUserRequestDto request) {
		User findUser = userRepository.findUserById(authUser.getId())
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

		if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
			throw new ResponseStatusException(INVALID_PASSWORD.getStatus(),INVALID_PASSWORD.getMessage());
		}

		findUser.deleteUser();
	}

}
