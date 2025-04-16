package com.example.hipreader.domain.bookscore.batch;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.bookscore.entity.BookScore;
import com.example.hipreader.domain.bookscore.repository.BookScoreRepository;

import com.example.hipreader.domain.userbook.repository.UserBookRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookScoreInitializer {

	private final UserBookRepository userBookRepository;
	private final BookScoreRepository bookScoreRepository;


	@PostConstruct
	@Transactional
	public void init() {
		userBookRepository.findAll().forEach(ub -> {
			Book book = ub.getBook(); // UserBook에서 Book 객체 직접 참조
			BookScore score = bookScoreRepository.findByBook(book)
				.orElseGet(() -> BookScore.builder()
					.book(book) // Book 객체로 생성
					.build());

			score.increment(ub.getStatus());
			bookScoreRepository.save(score);
		});
	}
}
