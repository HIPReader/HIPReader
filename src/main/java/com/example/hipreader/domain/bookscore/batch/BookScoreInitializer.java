package com.example.hipreader.domain.bookscore.batch;

import static com.example.hipreader.domain.userbook.status.Status.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
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
			BookScore score = bookScoreRepository.findById(ub.getBook().getId())
				.orElseGet(() -> BookScore.builder()
					.bookId(ub.getBook().getId())
					.build());

			score.increment(ub.getStatus());
			bookScoreRepository.save(score);
		});
	}
}
