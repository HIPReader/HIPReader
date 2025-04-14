package com.example.hipreader.domain.review.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.review.dto.request.ReviewCreateRequestDto;
import com.example.hipreader.domain.review.dto.request.ReviewUpdateRequestDto;
import com.example.hipreader.domain.review.dto.response.ReviewCreateResponseDto;
import com.example.hipreader.domain.review.dto.response.ReviewReadResponseDto;
import com.example.hipreader.domain.review.dto.response.ReviewUpdateResponseDto;
import com.example.hipreader.domain.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/reviews")
	public ResponseEntity<ReviewCreateResponseDto> createReview(
		@RequestBody @Valid ReviewCreateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser
	) {

		return new ResponseEntity<>(reviewService.createReview(requestDto, authUser), HttpStatus.CREATED);
	}

	@GetMapping("/books/{bookId}/reviews")
	public ResponseEntity<List<ReviewReadResponseDto>> getReviews(@PathVariable Long bookId) {
		return new ResponseEntity<>(reviewService.getReviews(bookId), HttpStatus.OK);
	}

	@GetMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<ReviewReadResponseDto> getReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId
	) {
		return new ResponseEntity<>(reviewService.getReview(bookId, reviewId), HttpStatus.OK);
	}

	@PatchMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<ReviewUpdateResponseDto> updateReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId,
		@RequestBody ReviewUpdateRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser
	) {
		return new ResponseEntity<>(reviewService.updateReview(bookId, reviewId, requestDto, authUser), HttpStatus.OK);
	}

	@DeleteMapping("/books/{bookId}/reviews/{reviewId}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable Long bookId,
		@PathVariable Long reviewId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		reviewService.deleteReview(bookId, reviewId, authUser);
		return ResponseEntity.noContent().build();
	}
}
