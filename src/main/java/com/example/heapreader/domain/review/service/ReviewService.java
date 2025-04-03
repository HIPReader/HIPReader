package com.example.heapreader.domain.review.service;

import com.example.heapreader.domain.review.dto.request.ReviewRequestDto;
import com.example.heapreader.domain.review.dto.response.ReviewResponseDto;
import com.example.heapreader.domain.review.entity.Review;
import com.example.heapreader.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // review 생성
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {

        Review review = Review.builder()
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.toDto(savedReview);
    }

    // review 다건 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(review -> new ReviewResponseDto(review.getId(), review.getContent(), review.getRating()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("Review not found with id " + reviewId)
        );

        return ReviewResponseDto.toDto(review);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("Review not found with id " + reviewId)
        );

        if (requestDto.getContent() != null) {
            review.updateContent(requestDto.getContent());
        }

        if (requestDto.getRating() != null) {
            review.updateRating(requestDto.getRating());
        }
        return ReviewResponseDto.toDto(review);
    }
}
