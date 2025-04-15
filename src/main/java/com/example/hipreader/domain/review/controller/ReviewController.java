package com.example.hipreader.domain.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.review.dto.request.CreateReviewRequestDto;
import com.example.hipreader.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.hipreader.domain.review.dto.response.CreateReviewResponseDto;
import com.example.hipreader.domain.review.dto.response.ReadReviewResponseDto;
import com.example.hipreader.domain.review.dto.response.UpdateReviewResponseDto;
import com.example.hipreader.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/reviews")
	public ResponseEntity<CreateReviewResponseDto> createReview(
		@RequestBody @Valid CreateReviewRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser
	) {

		CreateReviewResponseDto createReviewResponseDto = reviewService.createReview(requestDto, authUser);
		return new ResponseEntity<>(createReviewResponseDto, HttpStatus.CREATED);
	}

	@GetMapping("/books/{bookId}/reviews")
	public ResponseEntity<Page<ReadReviewResponseDto>> getReviews(
		@PathVariable Long bookId,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt,desc") String sort
	) {
		Page<ReadReviewResponseDto> readReviewResponseDtos = reviewService.getReviews(bookId, page, size, sort);
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
		@RequestBody UpdateReviewRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser
	) {

		UpdateReviewResponseDto updateReviewResponseDto = reviewService.updateReview(bookId, reviewId, requestDto,
			authUser);
		return new ResponseEntity<>(updateReviewResponseDto, HttpStatus.OK);
	}

	@DeleteMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		reviewService.deleteReview(bookId, reviewId, authUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
