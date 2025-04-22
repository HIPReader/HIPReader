package com.example.hipreader.domain.bookscore.service;

import static com.example.hipreader.common.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.entity.YearlyBookScore;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;
import com.example.hipreader.domain.bookscore.repository.YearlyBookScoreRepository;
import com.example.hipreader.domain.userbook.status.Status;

@ExtendWith(MockitoExtension.class)
class BookScoreServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookScoreRepository bookScoreRepository;

	@Mock
	private YearlyBookScoreRepository yearlyBookScoreRepository;

	@InjectMocks
	private BookScoreService bookScoreService;

	private Book currentYearBook;
	private Book oldYearBook;
	private BookScore bookScore;

	@BeforeEach
	void setUp() {
		currentYearBook = Book.builder()
			.id(1L)
			.datePublished(LocalDate.now()) // 올해 출간된 책
			.build();

		oldYearBook = Book.builder()
			.id(2L)
			.datePublished(LocalDate.now().minusYears(1)) // 작년 출간된 책
			.build();

		bookScore = BookScore.builder()
			.book(currentYearBook)
			.toRead(0L)
			.reading(0L)
			.finished(0L)
			.build();
	}

	@Test
	@DisplayName("올해 출간된 책의 상태 변경 시 BookScore와 YearlyBookScore가 업데이트됨")
	void handleStatusChange_CurrentYearBook_UpdatesBothScores() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.of(currentYearBook));
		when(bookScoreRepository.findByBook(currentYearBook)).thenReturn(Optional.of(bookScore));
		when(yearlyBookScoreRepository.findByYear(anyInt()))
			.thenReturn(Optional.empty());

		// When
		bookScoreService.handleStatusChange(1L, Status.TO_READ, Status.READING);

		// Then: BookScore가 업데이트되었는지 확인
		assertEquals(1L, bookScore.getReading());
		verify(bookScoreRepository).save(bookScore);

		// Then: YearlyBookScore가 저장되었는지 확인
		verify(yearlyBookScoreRepository).save(any(YearlyBookScore.class));
	}

	@Test
	@DisplayName("작년 출간된 책의 상태 변경 시 YearlyBookScore 업데이트 안 됨")
	void handleStatusChange_OldYearBook_DoesNotUpdateYearlyScore() {
		// Given
		when(bookRepository.findById(2L)).thenReturn(Optional.of(oldYearBook));
		when(bookScoreRepository.findByBook(oldYearBook)).thenReturn(Optional.of(bookScore));

		// When
		bookScoreService.handleStatusChange(2L, Status.TO_READ, Status.READING);

		// Then: YearlyBookScore 저장되지 않음
		verify(yearlyBookScoreRepository, never()).save(any());
	}

	@Test
	@DisplayName("YearlyBookScore에 더 높은 점수가 있을 때 업데이트")
	void handleStatusChange_HigherScore_UpdatesYearlyBookScore() {
		// Given
		YearlyBookScore existingYearlyScore = YearlyBookScore.builder()
			.year(LocalDate.now().getYear())
			.book(currentYearBook)
			.totalScore(100L)
			.build();

		BookScore highScoreBook = BookScore.builder()
			.book(currentYearBook)
			.toRead(0L)
			.reading(0L)
			.finished(50L) // finished *3 = 150점
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(currentYearBook));
		when(bookScoreRepository.findByBook(currentYearBook)).thenReturn(Optional.of(highScoreBook));
		when(yearlyBookScoreRepository.findByYear(anyInt()))
			.thenReturn(Optional.of(existingYearlyScore));

		// When: 상태 변경 (실제로는 점수 변동 없음. 기존 점수가 높은 경우 테스트)
		bookScoreService.handleStatusChange(1L, Status.FINISHED, Status.FINISHED);

		// Then: YearlyBookScore가 업데이트됨
		assertEquals(150L, existingYearlyScore.getTotalScore());
		verify(yearlyBookScoreRepository).save(existingYearlyScore);
	}


	@Test
	@DisplayName("올해의 책이 없을 때 예외 발생")
	void getBookOfTheYear_NotFound_ThrowsException() {
		// Given
		int currentYear = LocalDate.now().getYear();
		when(yearlyBookScoreRepository.findByYear(currentYear))
			.thenReturn(Optional.empty());

		// When & Then
		NotFoundException exception = assertThrows(
			NotFoundException.class,
			() -> bookScoreService.getBookOfTheYear()
		);
		assertEquals(BOOK_NOT_PUBLISHED, exception.getErrorCode());
	}
}
