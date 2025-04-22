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
@Transactional
public class UserCommandService {

	private final UserRepository userRepository;
	private final PasswordValidator passwordValidator;

	public UpdateUserResponseDto updateUser(AuthUser authUser, UpdateUserRequestDto updateUserRequestDto) {
		User user = findUserOrElseThrow(authUser.getId());

		user.patchProfile(updateUserRequestDto.getNickname(),updateUserRequestDto.getAge(),updateUserRequestDto.getGender());

		return UpdateUserResponseDto.toDto(user);
	}

	public void changePassword(AuthUser authUser, ChangePasswordRequestDto changePasswordRequestDto) {
		User user = findUserOrElseThrow(authUser.getId());

		passwordValidator.validateOldPassword(changePasswordRequestDto.getOldPassword(), user.getPassword());
		passwordValidator.validate(changePasswordRequestDto.getNewPassword(), user.getPassword());
		user.changePassword(passwordValidator.encode(changePasswordRequestDto.getNewPassword()));
	}

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
