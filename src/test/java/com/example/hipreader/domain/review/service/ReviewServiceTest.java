package com.example.hipreader.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.hipreader.auth.dto.AuthUser;
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
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BookRepository bookRepository;

	@Test
	void createReview_success() {
		// given
		Long userId = 1L;
		Long bookId = 100L;
		CreateReviewRequestDto requestDto = CreateReviewRequestDto.builder()
			.content("Great book")
			.rating(5)
			.userId(userId)
			.bookId(bookId)
			.build();
		AuthUser authUser = new AuthUser(userId, "test@example.com", UserRole.ROLE_USER);

		User mockUser = User.builder()
			.id(userId)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(25)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(100L)
			.title("Test Book")
			.isbn13("1234567890123")
			.publisher("Test Publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.totalPages(300)
			.coverImage("test.jpg")
			.categoryName("Fiction")
			.build();

		Review mockReview = Review.builder()
			.content("Great book")
			.rating(5)
			.user(mockUser)
			.book(mockBook)
			.build();

		given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
		given(bookRepository.findById(bookId)).willReturn(Optional.of(mockBook));
		given(reviewRepository.existsByUserIdAndBookId(userId, bookId)).willReturn(false);
		given(reviewRepository.save(any(Review.class))).willReturn(mockReview);

		// when
		CreateReviewResponseDto responseDto = reviewService.createReview(requestDto, authUser);

		// then
		assertThat(responseDto.getContent()).isEqualTo("Great book");
		assertThat(responseDto.getRating()).isEqualTo(5);
	}

	@Test
	void getReviews_success() {
		// given
		Long bookId = 1L;
		int page = 1;
		int size = 10;
		String sort = "createdAt,desc";

		User mockUser = User.builder()
			.id(1L)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(25)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(bookId)
			.title("Test Book")
			.isbn13("1234567890123")
			.publisher("Publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Review mockReview = Review.builder()
			.id(100L)
			.content("Nice Book")
			.rating(4)
			.user(mockUser)
			.book(mockBook)
			.build();

		Page<Review> reviewPage = new PageImpl<>(List.of(mockReview));
		Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		given(reviewRepository.findByBookIdWithUserAndBook(bookId, pageable)).willReturn(reviewPage);

		// when
		Page<ReadReviewResponseDto> result = reviewService.getReviews(bookId, page, size, sort);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("Nice Book");
		assertThat(result.getContent().get(0).getRating()).isEqualTo(4);
	}

	@Test
	void getReview_success() {
		// given
		Long bookId = 1L;
		Long reviewId = 100L;

		User mockUser = User.builder()
			.id(1L)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(25)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(bookId)
			.title("Book Title")
			.isbn13("1234567890123")
			.publisher("publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Review mockReview = Review.builder()
			.id(reviewId)
			.content("Interesting read")
			.rating(4)
			.user(mockUser)
			.book(mockBook)
			.build();

		given(reviewRepository.findByIdAndBook_id(reviewId, bookId)).willReturn(Optional.of(mockReview));

		// when
		ReadReviewResponseDto result = reviewService.getReview(bookId, reviewId);

		// then
		assertThat(result.getContent()).isEqualTo("Interesting read");
		assertThat(result.getRating()).isEqualTo(4);
	}

	@Test
	void updateReview_success() {
		// given
		Long bookId = 1L;
		Long reviewId = 100L;
		Long userId = 1L;

		UpdateReviewRequestDto requestDto = UpdateReviewRequestDto.builder()
			.content("Updated review content")
			.rating(4)
			.userId(userId)
			.bookId(bookId)
			.build();

		AuthUser authUser = new AuthUser(userId, "user@example.com", UserRole.ROLE_USER);

		User mockUser = User.builder()
			.id(userId)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(25)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(bookId)
			.title("Test Book")
			.isbn13("1234567890123")
			.publisher("publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Review mockReview = Review.builder()
			.id(reviewId)
			.content("Original content")
			.rating(3)
			.user(mockUser)
			.book(mockBook)
			.build();

		given(reviewRepository.findByIdAndBook_id(reviewId, bookId)).willReturn(Optional.of(mockReview));

		// when
		UpdateReviewResponseDto result = reviewService.updateReview(bookId, reviewId, requestDto, authUser);

		// then
		assertThat(result.getContent()).isEqualTo("Updated review content");
		assertThat(result.getRating()).isEqualTo(4);
	}

	@Test
	void deleteReview_success() {
		// given
		Long bookId = 1L;
		Long reviewId = 100L;
		Long userId = 1L;

		AuthUser authUser = new AuthUser(userId, "user@example.com", UserRole.ROLE_USER);

		User mockUser = User.builder()
			.id(userId)
			.nickname("testUser")
			.email("test@example.com")
			.password("password123")
			.age(25)
			.role(UserRole.ROLE_USER)
			.gender(Gender.MALE)
			.build();

		Book mockBook = Book.builder()
			.id(bookId)
			.title("Book Title")
			.isbn13("1234567890123")
			.publisher("publisher")
			.datePublished(LocalDate.of(2023, 1, 1))
			.build();

		Review mockReview = Review.builder()
			.id(reviewId)
			.content("Some content")
			.rating(5)
			.user(mockUser)
			.book(mockBook)
			.build();

		given(reviewRepository.findByIdAndBook_id(reviewId, bookId)).willReturn(Optional.of(mockReview));

		// when
		reviewService.deleteReview(bookId, reviewId, authUser);

		// then
		verify(reviewRepository).delete(mockReview);
	}
}
