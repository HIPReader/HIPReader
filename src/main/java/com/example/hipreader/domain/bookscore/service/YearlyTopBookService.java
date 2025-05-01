package com.example.hipreader.domain.bookscore.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
@CacheConfig(cacheNames = "yearlyTopBooks") // 클래스 전체 캐시 설정
public class YearlyTopBookService {
	private final YearlyTopBookRepository yearlyTopBookRepository;

	@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 100))
	@Transactional(timeout = 30)
	@CacheEvict(key = "T(java.time.LocalDate).now().getYear()") // 해당 년도 캐시 삭제
	public void updateYearlyTopBook(Book book, BookScore score) {
		int currentYear = LocalDate.now().getYear();
		if (book.getDatePublished().getYear() != currentYear) return;

		long totalScore = score.getTotalScore();
		List<YearlyTopBook> currentTopBooks = yearlyTopBookRepository.findByYear(currentYear);
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
