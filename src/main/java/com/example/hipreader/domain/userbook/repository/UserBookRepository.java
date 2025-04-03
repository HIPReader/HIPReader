package com.example.hipreader.domain.userbook.repository;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userbook.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {
    boolean existsByUserandBook(User user, Book book);
}
