package com.example.hipreader.domain.bookscore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.bookscore.entity.BookScore;

public interface BookScoreRepository extends JpaRepository<BookScore, Long> {
	@Query("SELECT bs FROM BookScore bs WHERE bs.book = :book")
	Optional<BookScore> findByBook(Book book);
}
