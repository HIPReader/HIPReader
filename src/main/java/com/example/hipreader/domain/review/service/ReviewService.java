package com.example.hipreader.domain.review.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.ConflictException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.review.dto.request.ReviewCreateRequestDto;
import com.example.hipreader.domain.review.dto.request.ReviewUpdateRequestDto;
import com.example.hipreader.domain.review.dto.response.ReviewCreateResponseDto;
import com.example.hipreader.domain.review.dto.response.ReviewReadResponseDto;
import com.example.hipreader.domain.review.dto.response.ReviewUpdateResponseDto;
import com.example.hipreader.domain.review.entity.Review;
import com.example.hipreader.domain.review.repository.ReviewRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final BookRepository bookRepository;

	// review 생성
	@Transactional
	public ReviewCreateResponseDto createReview(ReviewCreateRequestDto requestDto, AuthUser authUser) {
		User user = userRepository.findById(authUser.getId()).orElseThrow(
			() -> new NotFoundException(USER_NOT_FOUND)
		);
		Book book = bookRepository.findById(requestDto.getBookId()).orElseThrow(
			() -> new NotFoundException(BOOK_NOT_FOUND)
		);

		if (reviewRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
			throw new ConflictException(REVIEW_ALREADY_EXISTS);
		}

		Review review = Review.builder()
			.content(requestDto.getContent())
			.rating(requestDto.getRating())
			.user(user)
			.book(book)
			.build();

		Review savedReview = reviewRepository.save(review);

		return ReviewCreateResponseDto.toDto(savedReview);
	}

	// review 다건 조회
	@Transactional(readOnly = true)
	public List<ReviewReadResponseDto> getReviews(Long bookId) {
		List<Review> reviews = reviewRepository.findAllByBook_id(bookId);
		return reviews.stream()
			.map(ReviewReadResponseDto::toDto)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ReviewReadResponseDto getReview(Long bookId, Long reviewId) {
		Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
			() -> new NotFoundException(REVIEW_NOT_FOUND)
		);

		return ReviewReadResponseDto.toDto(review);
	}

	@Transactional
	public ReviewUpdateResponseDto updateReview(Long bookId, Long reviewId, ReviewUpdateRequestDto requestDto,
		AuthUser authUser) {
		Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
			() -> new NotFoundException(REVIEW_NOT_FOUND)
		);

		if (!review.getUser().getId().equals(authUser.getId())) {
			throw new UnauthorizedException(REVIEW_UNAUTHORIZED);
		}

		if (requestDto.getContent() != null) {
			review.updateContent(requestDto.getContent());
		}

		if (requestDto.getRating() != null) {
			review.updateRating(requestDto.getRating());
		}

		return ReviewUpdateResponseDto.toDto(review);
	}

	@Transactional
	public void deleteReview(Long bookId, Long reviewId, AuthUser authUser) {
		Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
			() -> new NotFoundException(REVIEW_NOT_FOUND)
		);

		if (!review.getUser().getId().equals(authUser.getId())) {
			throw new UnauthorizedException(REVIEW_UNAUTHORIZED);
		}

		reviewRepository.delete(review);
	}
}
