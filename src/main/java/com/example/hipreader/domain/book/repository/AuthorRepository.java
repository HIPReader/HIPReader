package com.example.hipreader.domain.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hipreader.domain.book.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

	List<Author> findAuthorsByBookId(Long bookId);
}
