package com.example.hipreader.domain.userbook.controller;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userbook.dto.request.RegisterUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.request.UpdateUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.response.UserBookResponseDto;
import com.example.hipreader.domain.userbook.service.UserBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user_books")
@RequiredArgsConstructor
public class UserBookController {

    private final UserBookService userBookService;

    @PostMapping
    public ResponseEntity<UserBookResponseDto> registerUserBook(
            @AuthenticationPrincipal AuthUser authUser, @RequestBody RegisterUserBookRequestDto registerUserBookRequestDto
    ) {
        return ResponseEntity.ok(userBookService.registerUserBook(authUser, registerUserBookRequestDto));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<UserBookResponseDto>> getReadingBooks(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userBookService.findReadingBooks(authUser, page, size));
    }

    @GetMapping("/{userbookId}")
    public ResponseEntity<UserBookResponseDto> getReadingBook(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long userbookId) {
        return ResponseEntity.ok(userBookService.findReadingBook(authUser, userbookId));
    }

    @PatchMapping("/userbookId")
    public ResponseEntity<UserBookResponseDto> updateUserBook(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody UpdateUserBookRequestDto updateUserBookRequestDto,
            @RequestParam Long userbookId
            ) {
        return ResponseEntity.ok(userBookService.updateUserBook(authUser, updateUserBookRequestDto, userbookId));
    }

    @DeleteMapping("/userbookId")
    public ResponseEntity<Void> deleteUserBook(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long userbookId) {
        userBookService.deleteUserBook(authUser, userbookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my/wish")
    public ResponseEntity<Page<UserBookResponseDto>> getWishBooks(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(userBookService.findWishBooks(authUser, page, size));
    }
}
