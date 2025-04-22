package com.example.hipreader.domain.userbook.controller;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userbook.dto.response.RegisterUSerBookResponseDto;
import com.example.hipreader.domain.userbook.dto.request.RegisterUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.request.UpdateUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.response.GetUserBookResponseDto;
import com.example.hipreader.domain.userbook.dto.response.UpdateUserBookResponseDto;
import com.example.hipreader.domain.userbook.service.UserBookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user_books")
@RequiredArgsConstructor
public class UserBookController {

	private final UserBookService userBookService;

	@PostMapping
	public ResponseEntity<RegisterUSerBookResponseDto> registerUserBook(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody RegisterUserBookRequestDto registerUserBookRequestDto
	) {
		RegisterUSerBookResponseDto registerUSerBookResponseDto = userBookService.registerUserBook(authUser, registerUserBookRequestDto);
		return new ResponseEntity<>(registerUSerBookResponseDto,HttpStatus.CREATED);
	}

	@GetMapping("/my")
	public ResponseEntity<Page<GetUserBookResponseDto>> getReadingBooks(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(userBookService.findReadingBooks(authUser, page, size));
	}

	@GetMapping("/{userbookId}")
	public ResponseEntity<GetUserBookResponseDto> getReadingBook(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long userbookId
	) {
		GetUserBookResponseDto getUserBookResponseDto = userBookService.findReadingBook(authUser, userbookId);

		return new ResponseEntity<>(getUserBookResponseDto,HttpStatus.OK);
	}

	@PatchMapping("/{userbookId}")
	public ResponseEntity<UpdateUserBookResponseDto> updateUserBook(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody UpdateUserBookRequestDto updateUserBookRequestDto,
		@PathVariable Long userbookId
	) {
		UpdateUserBookResponseDto updateUserBookResponseDto = userBookService.updateUserBook(authUser, updateUserBookRequestDto, userbookId);

		return new ResponseEntity<>(updateUserBookResponseDto,HttpStatus.OK);
	}

	@DeleteMapping("/{userbookId}")
	public ResponseEntity<Void> deleteUserBook(@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long userbookId
	) {
		userBookService.deleteUserBook(authUser, userbookId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/my/wish")
	public ResponseEntity<Page<GetUserBookResponseDto>> getWishBooks(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		userBookService.findWishBooks(authUser, page, size);

        return new ResponseEntity<>(userBookService.findWishBooks(authUser, page, size), HttpStatus.OK);
	}
}
