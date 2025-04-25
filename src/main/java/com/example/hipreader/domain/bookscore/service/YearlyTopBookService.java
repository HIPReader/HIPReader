package com.example.hipreader.domain.bookscore.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;
import com.example.hipreader.domain.bookscore.repository.YearlyTopBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YearlyTopBookService {
	private final YearlyTopBookRepository yearlyTopBookRepository;

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 100))
	@Transactional(timeout = 30)
	public void updateYearlyTopBook(Book book, BookScore score) {
		int currentYear = LocalDate.now().getYear();
		if (book.getDatePublished().getYear() != currentYear) return;

		long totalScore = score.getTotalScore();
		List<YearlyTopBook> currentTopBooks = yearlyTopBookRepository.findByYearWithLock(currentYear);
		long currentMaxScore = currentTopBooks.stream()
			.mapToLong(YearlyTopBook::getTotalScore)
			.max().orElse(0L);

		if (totalScore >= currentMaxScore) {
			if (totalScore > currentMaxScore) {
				yearlyTopBookRepository.deleteAllByYear(currentYear);
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
