package com.example.heapreader.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.heapreader.auth.dto.AuthUser;
import com.example.heapreader.domain.user.dto.request.ChangePasswordRequestDto;
import com.example.heapreader.domain.user.dto.request.DeleteUserRequestDto;
import com.example.heapreader.domain.user.dto.response.GetUserResponseDto;
import com.example.heapreader.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/{userId}")
	public ResponseEntity<GetUserResponseDto> getUser(
		@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getUser(userId));
	}

	@PatchMapping("/password")
	public void changePassword(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
		userService.changePassword(authUser.getId(), changePasswordRequestDto);
	}

	@DeleteMapping("/{userId}")
	public void deleteUser(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody DeleteUserRequestDto userDeleteRequestDto) {
		userService.deleteUser(authUser, userDeleteRequestDto);
	}

}
