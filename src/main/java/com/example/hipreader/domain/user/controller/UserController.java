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
import com.example.hipreader.domain.user.dto.response.GetUserResponseDto;
import com.example.hipreader.domain.user.service.UserService;
import com.example.hipreader.domain.userbook.dto.response.UserBookResponseDto;

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

	@GetMapping
	public ResponseEntity<Page<GetUserResponseDto>> getUsers(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(userService.getUsers(page, size));
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
