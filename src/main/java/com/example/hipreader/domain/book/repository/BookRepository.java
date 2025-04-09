package com.example.hipreader.domain.book.repository;

import com.example.hipreader.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn13(String isbn13);
}
