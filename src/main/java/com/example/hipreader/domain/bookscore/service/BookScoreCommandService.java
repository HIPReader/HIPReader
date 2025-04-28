package com.example.hipreader.domain.bookscore.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;
import com.example.hipreader.domain.userbook.status.Status;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookScoreCommandService {
	private final BookRepository bookRepository;
	private final BookScoreRepository bookScoreRepository;

	public BookScore updateBookScore(Long bookId, Status oldStatus, Status newStatus) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new NotFoundException(BOOK_NOT_FOUND));
		BookScore score = bookScoreRepository.findByBook(book)
			.orElseGet(() -> BookScore.builder().book(book).build());
		score.updateCount(oldStatus, newStatus);
		return bookScoreRepository.save(score);
	}
}
