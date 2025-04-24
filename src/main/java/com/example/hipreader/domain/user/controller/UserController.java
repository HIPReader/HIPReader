package com.example.hipreader.domain.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.hipreader.domain.user.dto.request.DeleteUserRequestDto;
import com.example.hipreader.domain.user.dto.request.UpdateUserRequestDto;
import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.dto.response.UpdateUserResponseDto;
import com.example.hipreader.domain.user.service.UserCommandService;
import com.example.hipreader.domain.user.service.UserQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserCommandService userCommandService;
	private final UserQueryService userQueryService;

	@GetMapping("/v1/users/{userId}")
	public ResponseEntity<GetUserResponseDto> getUser(
		@PathVariable @Valid Long userId) {
		GetUserResponseDto getUserResponseDto = userQueryService.getUser(userId);

		return new ResponseEntity<>(getUserResponseDto, HttpStatus.OK);
	}

	@GetMapping("/v1/users")
	public ResponseEntity<Page<GetUserResponseDto>> getAllUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		Page<GetUserResponseDto> getUserResponseDto = userQueryService.getAllUsers(page, size);

		return new ResponseEntity<>(getUserResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/v1/users")
	public ResponseEntity<UpdateUserResponseDto> patchUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid UpdateUserRequestDto requestDto) {
		UpdateUserResponseDto updateUserResponseDto = userCommandService.updateUser(authUser, requestDto);

		return new ResponseEntity<>(updateUserResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/v1/users/password")
	public ResponseEntity<Void> changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid ChangePasswordRequestDto changePasswordRequestDto) {
		userCommandService.changePassword(authUser, changePasswordRequestDto);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping("/v1/users/{userId}")
	public ResponseEntity<Void> deleteUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody @Valid DeleteUserRequestDto userDeleteRequestDto) {
		userCommandService.deleteUser(authUser, userDeleteRequestDto);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
