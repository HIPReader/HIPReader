package com.example.hipreader.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.book.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
