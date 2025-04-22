package com.example.hipreader.domain.user.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.filter.PasswordValidator;
import com.example.hipreader.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.hipreader.domain.user.dto.request.DeleteUserRequestDto;
import com.example.hipreader.domain.user.dto.request.UpdateUserRequestDto;
import com.example.hipreader.domain.user.dto.response.UpdateUserResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCommandService {

	private final UserRepository userRepository;
	private final PasswordValidator passwordValidator;

	@Transactional
	public UpdateUserResponseDto updateUser(AuthUser authUser, UpdateUserRequestDto updateUserRequestDto) {
		User user = findUserOrElseThrow(authUser.getId());

		user.updateProfile(updateUserRequestDto.getNickname(),updateUserRequestDto.getAge(),updateUserRequestDto.getGender());

		return UpdateUserResponseDto.toDto(user);
	}

	@Transactional
	public void changePassword(AuthUser authUser, ChangePasswordRequestDto changePasswordRequestDto) {
		User user = findUserOrElseThrow(authUser.getId());

		passwordValidator.validateOldPassword(changePasswordRequestDto.getOldPassword(), user.getPassword());
		passwordValidator.validate(changePasswordRequestDto.getNewPassword(), user.getPassword());
		user.changePassword(passwordValidator.encode(changePasswordRequestDto.getNewPassword()));
	}

	@Transactional
	public void deleteUser(AuthUser authUser, DeleteUserRequestDto request) {
		User finduser = findUserOrElseThrow(authUser.getId());

		passwordValidator.validateOldPassword(request.getPassword(), finduser.getPassword());
		finduser.deleteUser();
	}

	private User findUserOrElseThrow(Long userId) {
		return userRepository.findActiveUserById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}
}
