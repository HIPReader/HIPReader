package com.example.hipreader.domain.review.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.ConflictException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.common.exception.UnauthorizedException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.review.dto.request.CreateReviewRequestDto;
import com.example.hipreader.domain.review.dto.request.UpdateReviewRequestDto;
import com.example.hipreader.domain.review.dto.response.CreateReviewResponseDto;
import com.example.hipreader.domain.review.dto.response.ReadReviewResponseDto;
import com.example.hipreader.domain.review.dto.response.UpdateReviewResponseDto;
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
	public CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto, AuthUser authUser) {
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

		return CreateReviewResponseDto.toDto(savedReview);
	}

	// review 다건 조회
	@Transactional(readOnly = true)
	public Page<ReadReviewResponseDto> getReviews(Long bookId, int page, int size, String sort) {
		String[] sortParams = sort.split(",");
		Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
		String sortBy = sortParams[0].trim();

		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
		Page<Review> reviews = reviewRepository.findByBookIdWithUserAndBook(bookId, pageable);

		return reviews.map(ReadReviewResponseDto::toDto);
	}

	@Transactional(readOnly = true)
	public ReadReviewResponseDto getReview(Long bookId, Long reviewId) {
		Review review = reviewRepository.findByIdAndBook_id(reviewId, bookId).orElseThrow(
			() -> new NotFoundException(REVIEW_NOT_FOUND)
		);

		return ReadReviewResponseDto.toDto(review);
	}

	@Transactional
	public UpdateReviewResponseDto updateReview(Long bookId, Long reviewId, UpdateReviewRequestDto requestDto,
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
		if (requestDto.getContent() != null) {
			review.updateContent(requestDto.getContent());
		}

		if (requestDto.getRating() != null) {
			review.updateRating(requestDto.getRating());
		}

		return UpdateReviewResponseDto.toDto(review);
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
