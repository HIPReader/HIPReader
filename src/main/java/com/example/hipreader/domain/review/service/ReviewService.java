package com.example.hipreader.domain.review.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import com.example.hipreader.common.exception.ErrorCode;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.review.dto.request.ReviewRequestDto;
import com.example.hipreader.domain.review.dto.response.ReviewResponseDto;
import com.example.hipreader.domain.review.entity.Review;
import com.example.hipreader.domain.review.exception.ReviewException;
import com.example.hipreader.domain.review.repository.ReviewRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    // review 생성
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );
        Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
                () -> new NotFoundException(BOOK_NOT_FOUND)
        );

        Review review = Review.builder()
                .content(requestDto.getContent())
                .rating(requestDto.getRating())
                .user(user)
                .book(book)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.toDto(savedReview);
    }

    // review 다건 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviews(Long bookId) {
        List<Review> reviews = reviewRepository.findAllByBook_id(bookId);
        return reviews.stream()
                .map(ReviewResponseDto::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(Long bookId, Long reviewId) {
        Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
                () -> new NotFoundException(REVIEW_NOT_FOUND)
        );

        return ReviewResponseDto.toDto(review);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long bookId, Long reviewId, ReviewRequestDto requestDto) {
        Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
                () -> new NotFoundException(REVIEW_NOT_FOUND)
        );

        if (requestDto.getContent() != null) {
            review.updateContent(requestDto.getContent());
        }

        if (requestDto.getRating() != null) {
            review.updateRating(requestDto.getRating());
        }

        return ReviewResponseDto.toDto(review);
    }

    @Transactional
    public void deleteReview(Long bookId, Long reviewId) {
        Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
                () -> new NotFoundException(REVIEW_NOT_FOUND)
        );

        reviewRepository.delete(review);
    }
}
