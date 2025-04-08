package com.example.hipreader.domain.userbook.repository;

import java.util.List;
import java.util.Map;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.gender.Gender;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.status.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBookRepository extends JpaRepository<UserBook, Long>, UserBookRepositoryCustom {

	Page<UserBook> findByUser(User user, Pageable pageable);

	boolean existsByUserAndBook(User user, Book book);

	UserBook findByIdAndUser(long id, User user);

	Page<UserBook> findByUserAndStatus(User user, Status status, Pageable pageable);

	Map<Status, Long> countByBookIdGroupByStatus(Long id);
}
