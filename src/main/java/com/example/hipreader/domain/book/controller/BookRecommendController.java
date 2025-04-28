package com.example.hipreader.domain.book.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.response.BookRecommendResponseDto;
import com.example.hipreader.domain.book.service.BookRecommendService;
import com.example.hipreader.domain.user.vo.Gender;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookRecommendController {

	private final BookRecommendService bookRecommendService;

	// 연령별, 성별별, 카테고리별 책 추천 ( MySQL )
	@GetMapping("/v1/books/recommend")
	public ResponseEntity<PageResponseDto<BookRecommendResponseDto>> recommendBooksWithoutRedis(
		@RequestParam(required = false) Integer age,
		@RequestParam(required = false) Gender gender,
		@RequestParam(required = false) String categoryName,
		@PageableDefault(size = 10, page = 0) Pageable pageable
	) {
		PageResponseDto<BookRecommendResponseDto> responseDto = bookRecommendService.recommendBooksWithoutRedis(age,
			gender, categoryName, pageable);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	// 연령별, 성별별, 카테고리별 책 추천 ( Redis )
	@GetMapping("/v2/books/recommend")
	public ResponseEntity<PageResponseDto<BookRecommendResponseDto>> recommendBooksWithRedis(
		@RequestParam(required = false) Integer age,
		@RequestParam(required = false) Gender gender,
		@RequestParam(required = false) String categoryName,
		@PageableDefault(size = 10, page = 0) Pageable pageable
	) {
		PageResponseDto<BookRecommendResponseDto> responseDto = bookRecommendService.recommendBooksWithRedis(age,
			gender, categoryName, pageable);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	// 연령별, 성별별, 카테고리별 책 추천 ( Redis + ES )
	@GetMapping("/v3/books/recommend")
	public ResponseEntity<PageResponseDto<BookRecommendResponseDto>> recommendBooksWithRedisAndEs(
		@RequestParam(required = false) Integer age,
		@RequestParam(required = false) Gender gender,
		@RequestParam(required = false) String categoryName,
		@PageableDefault(size = 10, page = 0) Pageable pageable
	) {
		PageResponseDto<BookRecommendResponseDto> responseDto = bookRecommendService.recommendBooksWithRedisAndEs(age,
			gender, categoryName, pageable);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
