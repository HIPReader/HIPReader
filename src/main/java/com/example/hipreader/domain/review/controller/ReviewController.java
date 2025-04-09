package com.example.hipreader.domain.review.controller;

import com.example.hipreader.domain.review.dto.request.CreateReviewRequestDto;
import com.example.hipreader.domain.review.dto.response.ReadReviewResponseDto;
import com.example.hipreader.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.hipreader.domain.review.dto.response.CreateReviewResponseDto;
import com.example.hipreader.domain.review.dto.response.UpdateReviewResponseDto;
import com.example.hipreader.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/reviews")
	public ResponseEntity<CreateReviewResponseDto> createReview(
		@RequestBody @Valid CreateReviewRequestDto requestDto
	) {
		CreateReviewResponseDto createReviewResponseDto = reviewService.createReview(requestDto);
		return new ResponseEntity<>(createReviewResponseDto, HttpStatus.CREATED);
	}

	@GetMapping("/books/{bookId}/reviews")
	public ResponseEntity<List<ReadReviewResponseDto>> getReviews(
		@PathVariable Long bookId
	) {
		List<ReadReviewResponseDto> readReviewResponseDtos = reviewService.getReviews(bookId);
		return new ResponseEntity<>(readReviewResponseDtos, HttpStatus.OK);
	}

	@GetMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<ReadReviewResponseDto> getReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId
	) {
		ReadReviewResponseDto readReviewResponseDto = reviewService.getReview(bookId, reviewId);
		return new ResponseEntity<>(readReviewResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<UpdateReviewResponseDto> updateReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId,
		@RequestBody UpdateReviewRequestDto requestDto
	) {
		UpdateReviewResponseDto updateReviewResponseDto = reviewService.updateReview(bookId, reviewId, requestDto);
		return new ResponseEntity<>(updateReviewResponseDto, HttpStatus.OK);
	}

	@DeleteMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId
	) {
		reviewService.deleteReview(bookId, reviewId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
