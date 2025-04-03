package com.example.hipreader.domain.book.repository;

import com.example.hipreader.domain.book.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BooksRepository extends JpaRepository<Books, Long> {
}
