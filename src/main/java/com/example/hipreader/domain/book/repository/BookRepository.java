package com.example.hipreader.domain.book.repository;

import com.example.hipreader.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

}
