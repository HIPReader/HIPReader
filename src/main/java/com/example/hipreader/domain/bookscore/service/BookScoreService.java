package com.example.hipreader.domain.bookscore.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;
import com.example.hipreader.domain.bookscore.repository.YearlyTopBookRepository;
import com.example.hipreader.domain.userbook.status.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookScoreService {

	private final BookRepository bookRepository;
	private final BookScoreRepository bookScoreRepository;
	private final YearlyTopBookRepository yearlyTopBookRepository;

	@Transactional
	public void handleStatusChange(Long bookId, Status oldStatus, Status newStatus) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND));

		BookScore score = bookScoreRepository.findByBook(book)
			.orElseGet(() -> BookScore.builder().book(book).build());

		score.updateCount(oldStatus, newStatus);
		bookScoreRepository.save(score);

		LocalDate now = LocalDate.now();
		int currentYear = now.getYear();

		if (book.getDatePublished().getYear() == currentYear) {
			long totalScore = score.getTotalScore();

			List<YearlyTopBook> currentTopBooks = yearlyTopBookRepository.findByYear(currentYear);
			long currentMaxScore = currentTopBooks.stream()
				.mapToLong(YearlyTopBook::getTotalScore)
				.max().orElse(0L);

			if (totalScore >= currentMaxScore) {
				if (totalScore > currentMaxScore) {
					yearlyTopBookRepository.deleteAll(currentTopBooks);
				}

				yearlyTopBookRepository.save(
					YearlyTopBook.builder()
						.year(currentYear)
						.book(book)
						.totalScore(totalScore)
						.build()
				);
			}
		}
	}

	@Transactional(readOnly = true)
	public GetBookOfYearResponseDto getBookOfTheYear() {
		int currentYear = LocalDate.now().getYear();
		List<YearlyTopBook> topBooks = yearlyTopBookRepository.findByYear(currentYear);

		return GetBookOfYearResponseDto.from(topBooks);
	}
}
