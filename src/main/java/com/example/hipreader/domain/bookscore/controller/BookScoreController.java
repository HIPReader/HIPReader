package com.example.hipreader.domain.bookscore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.service.BookScoreQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookScoreController {

	private final BookScoreQueryService bookScoreQueryService;

	@GetMapping("/api/v1/books/of-the-year/{year}")
	public ResponseEntity<GetBookOfYearResponseDto> getBookOfTheYear(
		@PathVariable int year
	) {
		GetBookOfYearResponseDto getBookOfYearResponseDto = bookScoreQueryService.getBookOfTheYear(year);
		return new ResponseEntity<>(getBookOfYearResponseDto, HttpStatus.OK);
	}
}
