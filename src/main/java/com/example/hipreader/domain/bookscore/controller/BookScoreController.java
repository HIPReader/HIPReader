package com.example.hipreader.domain.bookscore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.service.BookScoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookScoreController {

	private final BookScoreService bookScoreService;

	@GetMapping("/api/v1/books/of-the-year")
	public ResponseEntity<GetBookOfYearResponseDto> getBookOfTheYear() {
		GetBookOfYearResponseDto getBookOfYearResponseDto = bookScoreService.getBookOfTheYear();
		return new ResponseEntity<>(getBookOfYearResponseDto, HttpStatus.ACCEPTED);
	}
}
