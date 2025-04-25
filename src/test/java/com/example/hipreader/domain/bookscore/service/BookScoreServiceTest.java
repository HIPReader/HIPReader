package com.example.hipreader.domain.bookscore.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;
import com.example.hipreader.domain.bookscore.repository.YearlyTopBookRepository;
import com.example.hipreader.domain.userbook.status.Status;

@ExtendWith(MockitoExtension.class)
class BookScoreServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookScoreRepository bookScoreRepository;

	@Mock
	private YearlyTopBookRepository yearlyTopBookRepository;

	@InjectMocks
	private BookScoreService bookScoreService;

	private Book currentYearBook;
	private Book oldYearBook;
	private BookScore bookScore;
	private final int currentYear = LocalDate.now().getYear();

	@BeforeEach
	void setUp() {
		currentYearBook = Book.builder()
			.id(1L)
			.datePublished(LocalDate.now())
			.title("Current Year Book")
			.build();

		oldYearBook = Book.builder()
			.id(2L)
			.datePublished(LocalDate.now().minusYears(1))
			.title("Old Year Book")
			.build();

		bookScore = BookScore.builder()
			.book(currentYearBook)
			.toRead(0L)
			.reading(0L)
			.finished(0L)
			.build();
	}

	@Test
	@DisplayName("올해 출간된 책 상태 변경 - YearlyTopBook 업데이트")
	void handleStatusChange_CurrentYearBook_UpdatesYearlyTopBook() {
		// Given
		when(bookRepository.findById(1L)).thenReturn(Optional.of(currentYearBook));
		when(bookScoreRepository.findByBook(currentYearBook)).thenReturn(Optional.of(bookScore));
		when(yearlyTopBookRepository.findByYear(currentYear)).thenReturn(List.of());

		// When
		bookScoreService.handleStatusChange(1L, null, Status.TO_READ);

		// Then
		verify(bookScoreRepository).save(bookScore);
		verify(yearlyTopBookRepository).save(argThat(ytb ->
			ytb.getTotalScore() == 1 &&
				ytb.getBook().equals(currentYearBook)
		));
	}

	@Test
	@DisplayName("작년 출간된 책 상태 변경 - YearlyTopBook 업데이트 안 함")
	void handleStatusChange_OldYearBook_NoUpdate() {
		// Given
		when(bookRepository.findById(2L)).thenReturn(Optional.of(oldYearBook));
		when(bookScoreRepository.findByBook(oldYearBook)).thenReturn(Optional.of(bookScore));

		// When
		bookScoreService.handleStatusChange(2L, null, Status.READING);

		// Then
		verify(yearlyTopBookRepository, never()).save(any());
	}

	@Test
	@DisplayName("새 최고점 도달 시 기존 기록 삭제 후 새 기록 추가")
	void handleStatusChange_NewMaxScore_ReplacesExisting() {
		// Given
		YearlyTopBook existing = YearlyTopBook.builder()
			.totalScore(50)
			.year(currentYear)
			.book(oldYearBook)
			.build();

		BookScore newScore = BookScore.builder()
			.book(currentYearBook)
			.finished(19)  // 19 * 3 = 57 → 상태 변경 후 20 * 3 = 60
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(currentYearBook));
		when(bookScoreRepository.findByBook(currentYearBook)).thenReturn(Optional.of(newScore));
		when(yearlyTopBookRepository.findByYear(currentYear)).thenReturn(List.of(existing));

		// When
		bookScoreService.handleStatusChange(1L, null, Status.FINISHED);

		// Then
		verify(yearlyTopBookRepository).deleteAll(List.of(existing));

		ArgumentCaptor<YearlyTopBook> captor = ArgumentCaptor.forClass(YearlyTopBook.class);
		verify(yearlyTopBookRepository).save(captor.capture());
		YearlyTopBook saved = captor.getValue();
		assertEquals(60, saved.getTotalScore());
		assertEquals(currentYearBook, saved.getBook());
		assertEquals(currentYear, saved.getYear());
	}

	@Test
	@DisplayName("동일 최고점 도달 시 다중 기록 저장")
	void handleStatusChange_EqualScore_AddsNewEntry() {
		// Given
		YearlyTopBook existing = YearlyTopBook.builder()
			.totalScore(100)
			.year(currentYear)
			.book(oldYearBook)
			.build();

		BookScore newScore = BookScore.builder()
			.book(currentYearBook)
			.finished(32) // 32 → 33 (상태 변경 후)
			.toRead(1)
			.build();

		when(bookRepository.findById(1L)).thenReturn(Optional.of(currentYearBook));
		when(bookScoreRepository.findByBook(currentYearBook)).thenReturn(Optional.of(newScore));
		when(yearlyTopBookRepository.findByYear(currentYear)).thenReturn(List.of(existing));

		// When
		bookScoreService.handleStatusChange(1L, null, Status.FINISHED);

		// Then
		verify(yearlyTopBookRepository, never()).deleteAll();
		verify(yearlyTopBookRepository).save(argThat(ytb ->
			ytb.getTotalScore() == 100 &&
				ytb.getBook().equals(currentYearBook) &&
				ytb.getYear() == currentYear
		));
	}

	@Test
	@DisplayName("올해의 책 조회 - 다중 결과")
	void getBookOfTheYear_MultipleResults() {
		// Given
		YearlyTopBook top1 = YearlyTopBook.builder()
			.book(currentYearBook)
			.totalScore(100)
			.build();

		YearlyTopBook top2 = YearlyTopBook.builder()
			.book(oldYearBook)
			.totalScore(100)
			.build();

		when(yearlyTopBookRepository.findByYear(currentYear)).thenReturn(List.of(top1, top2));

		// When
		GetBookOfYearResponseDto result = bookScoreService.getBookOfTheYear();

		// Then
		assertEquals(2, result.topBooks().size());
		assertEquals(100, result.maxScore());
	}

	@Test
	@DisplayName("존재하지 않는 책 조회 시 예외 발생")
	void handleStatusChange_BookNotFound() {
		// Given
		when(bookRepository.findById(999L)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundException.class, () ->
			bookScoreService.handleStatusChange(999L, null, Status.TO_READ)
		);
	}

	@Test
	@DisplayName("빈 YearlyTopBook 조회 시 빈 리스트 반환")
	void getBookOfTheYear_EmptyResults() {
		// Given
		when(yearlyTopBookRepository.findByYear(currentYear)).thenReturn(List.of());

		// When
		GetBookOfYearResponseDto result = bookScoreService.getBookOfTheYear();

		// Then
		assertTrue(result.topBooks().isEmpty());
		assertEquals(0, result.maxScore());
	}
}
