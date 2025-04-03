package com.example.hipreader.domain.userbook.controller;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userbook.dto.request.UserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.response.UserBookResponseDto;
import com.example.hipreader.domain.userbook.service.UserBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user_books")
@RequiredArgsConstructor
public class UserBookController {

    private final UserBookService userBookService;

    @PostMapping
    public ResponseEntity<UserBookResponseDto> registerUserBook(@AuthenticationPrincipal AuthUser authUser, @RequestBody UserBookRequestDto userBookRequestDto) {
        return ResponseEntity.ok(userBookService.registerUserBook(authUser, userBookRequestDto));
    }
}
