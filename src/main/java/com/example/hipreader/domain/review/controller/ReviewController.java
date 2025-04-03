package com.example.hipreader.domain.review.controller;

import com.example.hipreader.domain.review.dto.request.ReviewRequestDto;
import com.example.hipreader.domain.review.dto.response.ReviewResponseDto;
import com.example.hipreader.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody @Valid ReviewRequestDto requestDto) {
        return ResponseEntity.ok(reviewService.createReview(requestDto));
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequestDto requestDto) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, requestDto));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
