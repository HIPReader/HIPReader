package com.example.hipreader.domain.bookscore.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;
import com.example.hipreader.domain.userbook.status.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookScoreService {

	private final BookRepository bookRepository;
	private final BookScoreRepository bookScoreRepository;

	@Transactional
	public void handleStatusChange(Long bookId, Status oldStatus, Status newStatus) {
		BookScore score = bookScoreRepository.findById(bookId)
			.orElseGet(() -> BookScore.builder().bookId(bookId).build());

		score.updateCount(oldStatus, newStatus);
		bookScoreRepository.save(score);
	}

	@Transactional(readOnly = true)
	public GetBookOfYearResponseDto getBookOfTheYear() {
		// 1. 올해 출판된 책 조회
		LocalDate start = LocalDate.now().withDayOfYear(1); // 올해 첫날
		LocalDate end = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear()); // 올해 마지막 날
		List<Book> currentYearBooks = bookRepository.findBooksByPublicationYear(start, end);

		if (currentYearBooks.isEmpty()) {
			throw new NotFoundException(BOOK_NOT_PUBLISHED);
		}

		// 2. 해당 책들의 점수 조회
		List<BookScore> scores = bookScoreRepository.findAllById(
			currentYearBooks.stream()
				.map(Book::getId)
				.toList()
		);

		// 3. 최고 점수 책 찾기
		BookScore topScore = scores.stream()
			.max(Comparator.comparingLong(BookScore::getTotalScore))
			.orElseThrow(() -> new NotFoundException(SCORE_NOT_FOUND));

		// 4. 최고 점수 책 정보 가져오기
		Book topBook = bookRepository.findById(topScore.getBookId())
			.orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND));

		// 5. DTO 변환 (from 메서드 사용)
		return GetBookOfYearResponseDto.from(topBook, topScore.getTotalScore());
	}
}
