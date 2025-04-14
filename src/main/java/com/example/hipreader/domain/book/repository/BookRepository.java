package com.example.hipreader.domain.book.repository;

import java.time.LocalDate;
import java.util.List;

import com.example.hipreader.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn13(String isbn13);
  
	@Query("SELECT b FROM Book b WHERE b.datePublished BETWEEN :start AND :end")
	List<Book> findBooksByPublicationYear(
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);

  List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

	@Query("SELECT b FROM Book b WHERE LOWER(REPLACE(b.title, ' ', '')) LIKE LOWER(CONCAT('%', :cleanedKeyword, '%'))")
	List<Book> findByTitleIgnoringSpaces(@Param("cleanedKeyword") String cleanedKeyword);
}
