package com.example.hipreader.domain.bookscore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.bookscore.entity.BookScore;

public interface BookScoreRepository extends JpaRepository<BookScore, Long> {
	@EntityGraph(attributePaths = "book.authors")
	Optional<BookScore> findByBook(Book book);
}
