package com.example.heapreader.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.heapreader.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
