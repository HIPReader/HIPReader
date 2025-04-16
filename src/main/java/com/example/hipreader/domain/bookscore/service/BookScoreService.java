package com.example.hipreader.domain.bookscore.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.entity.YearlyBookScore;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;
import com.example.hipreader.domain.bookscore.repository.YearlyBookScoreRepository;
import com.example.hipreader.domain.userbook.status.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookScoreService {

	private final BookRepository bookRepository;
	private final BookScoreRepository bookScoreRepository;
	private final YearlyBookScoreRepository yearlyBookScoreRepository;

	@Transactional
	public void handleStatusChange(Long bookId, Status oldStatus, Status newStatus) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND));

		BookScore score = bookScoreRepository.findByBook(book)
			.orElseGet(() -> BookScore.builder().book(book).build());

		score.updateCount(oldStatus, newStatus);
		bookScoreRepository.save(score);

		// 올해 출간된 책인지 확인 후 YearlyBookScore 업데이트
		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();

		if (book.getDatePublished().getYear() == currentYear) {
			long totalScore = score.getTotalScore();

			YearlyBookScore yearlyTopBook = yearlyBookScoreRepository.findByYear(currentYear)
				.orElseGet(() -> YearlyBookScore.builder()
					.year(currentYear)
					.book(book)
					.totalScore(totalScore)
					.build());

			if (totalScore > yearlyTopBook.getTotalScore()) {
				yearlyTopBook.updateTopBook(book, totalScore);
			}

			yearlyBookScoreRepository.save(yearlyTopBook);
		}
	}

	@Transactional(readOnly = true)
	public GetBookOfYearResponseDto getBookOfTheYear() {
		int currentYear = LocalDate.now().getYear();

		YearlyBookScore yearlyTopBook = yearlyBookScoreRepository.findByYear(currentYear)
			.orElseThrow(() -> new NotFoundException(BOOK_NOT_PUBLISHED));

		return GetBookOfYearResponseDto.from(
			yearlyTopBook.getBook(),
			yearlyTopBook.getTotalScore()
		);
	}
}
