package com.example.hipreader.domain.userbook.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.genre.Genre;
import com.example.hipreader.domain.user.vo.Gender;

public interface UserBookRepositoryCustom {
	Page<Book> findRecommendedBooks(Integer age, Gender gender, String categoryName, Pageable pageable);
}
