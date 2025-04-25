package com.example.hipreader.domain.bookscore.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.userbook.status.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookScoreFacadeService {

	private final BookScoreCommandService bookScoreCommandService;
	private final YearlyTopBookService yearlyTopBookService;
	private final BookRepository bookRepository;

	@Async("taskExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleStatusChange(Long bookId, Status oldStatus, Status newStatus) {
		BookScore score = bookScoreCommandService.updateBookScore(bookId, oldStatus, newStatus);
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND));
		yearlyTopBookService.updateYearlyTopBook(book, score);
	}
}
