package com.example.hipreader.domain.userbook.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.status.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserBookRepository extends JpaRepository<UserBook, Long>, UserBookRepositoryCustom {

	Page<UserBook> findByUser(User user, Pageable pageable);

	boolean existsByUserAndBook(User user, Book book);

	UserBook findByIdAndUser(long id, User user);

	@Query("SELECT ub FROM UserBook ub JOIN FETCH ub.user JOIN FETCH ub.book b JOIN FETCH b.authors a")
	List<UserBook> findAllWithUserAndBook();

	Page<UserBook> findByUserAndStatusAndCreatedAtBetween(User user, Status status, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, Pageable pageable);

	Page<UserBook> findByUserAndStatus(User user, Status status, Pageable pageable);

	List<UserBook> findByUserAndStatus(User user, Status status);
}
