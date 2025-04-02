package com.example.heapreader.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.heapreader.domain.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
