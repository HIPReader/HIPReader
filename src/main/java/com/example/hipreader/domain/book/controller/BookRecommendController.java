package com.example.hipreader.domain.book.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.response.BookRecommendGetResponseDto;
import com.example.hipreader.domain.book.service.BookRecommendService;
import com.example.hipreader.domain.user.gender.Gender;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookRecommendController {

	private final BookRecommendService bookRecommendService;

	// 연령별, 성별별 책 추천 ( 랭킹 )
	@GetMapping("/v1/books/recommend/age-gender")
	public ResponseEntity<PageResponseDto<BookRecommendGetResponseDto>> getBookRanking(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam int age,
		@RequestParam Gender gender
	) {
		PageResponseDto<BookRecommendGetResponseDto> responseDto = bookRecommendService.getPopularBooksByAgeAndGender(
			page, size, age, gender);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
