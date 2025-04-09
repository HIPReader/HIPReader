package com.example.hipreader.domain.user.controller;

import org.springframework.data.domain.Page;
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
import com.example.hipreader.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<GetUserResponseDto> getUser(
		@PathVariable Long userId) {
		GetUserResponseDto getUserResponseDto = userService.getUser(userId);
		return ResponseEntity.ok(getUserResponseDto);
	}

	@GetMapping
	public ResponseEntity<Page<GetUserResponseDto>> getUsers(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<GetUserResponseDto> getUserResponseDto = userService.getUsers(page, size);

		return ResponseEntity.ok(getUserResponseDto);
	}

	@PatchMapping
	public ResponseEntity<UpdateUserResponseDto> updateUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody UpdateUserRequestDto requestDto
	) {
		UpdateUserResponseDto updateUserResponseDto = userService.updateUser(authUser, requestDto);

		return ResponseEntity.ok(updateUserResponseDto);
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		userService.changePassword(authUser.getId(), changePasswordRequestDto);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody DeleteUserRequestDto userDeleteRequestDto) {
		userService.deleteUser(authUser, userDeleteRequestDto);

		return ResponseEntity.noContent().build();
	}

}
