package com.example.hipreader.domain.bookscore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.bookscore.entity.BookScore;

public interface BookScoreRepository extends JpaRepository<BookScore, Long> {
}
