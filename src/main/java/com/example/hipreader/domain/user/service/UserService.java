package com.example.hipreader.domain.user.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.hipreader.domain.user.dto.request.DeleteUserRequestDto;
import com.example.hipreader.domain.user.dto.request.UpdateUserRequestDto;
import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.dto.response.UpdateUserResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(readOnly = true)
	public GetUserResponseDto getUser(Long userId) {
		return userRepository.findUserDtoById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Page<GetUserResponseDto> getUsers(int page, int size) {
		PageRequest pageRequest = PageRequest.of(Math.max(0, page-1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));

		return userRepository.findAllUserDto(pageRequest);
	}

	@Transactional
	public UpdateUserResponseDto updateUser(AuthUser authUser, UpdateUserRequestDto updateUserRequestDto) {
		User user = findUserOrElseThrow(authUser.getId());

		user.updateProfile(updateUserRequestDto.getNickname(),updateUserRequestDto.getAge(),updateUserRequestDto.getGender());

		return UpdateUserResponseDto.toDto(user);
	}

	@Transactional
	public void changePassword(AuthUser authUser, ChangePasswordRequestDto changePasswordRequestDto) {
		validateNewPassword(changePasswordRequestDto);

		User user = findUserOrElseThrow(authUser.getId());

		if (!passwordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
			throw new NotFoundException(INVALID_PASSWORD);
		}
		if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), user.getPassword())) {
			throw new BadRequestException(PASSWORD_SAME_AS_OLD);
		}

		user.changePassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
	}

	@Transactional
	public void deleteUser(AuthUser authUser, DeleteUserRequestDto request) {
		User finduser = findUserOrElseThrow(authUser.getId());

		if (!passwordEncoder.matches(request.getPassword(), finduser.getPassword())) {
			throw new BadRequestException(INVALID_PASSWORD);
		}

		finduser.deleteUser();
	}

	private static void validateNewPassword(ChangePasswordRequestDto changePasswordRequestDto) {
		if (changePasswordRequestDto.getNewPassword().length() < 8 ||
			!changePasswordRequestDto.getNewPassword().matches(".*\\d.*") ||
			!changePasswordRequestDto.getNewPassword().matches(".*[A-Z].*")) {
			throw new BadRequestException(INVALID_NEW_PASSWORD_FORMAT);
		}
	}

	private User findUserOrElseThrow(Long userId) {
		return userRepository.findActiveUserById(userId)
			.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

}
