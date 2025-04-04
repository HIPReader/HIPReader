package com.example.hipreader.domain.userbook.repository;

import java.util.List;

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

public interface UserBookRepository extends JpaRepository<UserBook, Long> {

	Page<UserBook> findByUser(User user, Pageable pageable);

	boolean existsByUserAndBook(User user, Book book);

	UserBook findByIdAndUser(long id, User user);

	Page<UserBook> findByUserAndStatus(User user, Status status, Pageable pageable);

	@Query("SELECT ub.book FROM UserBook ub "
		+ "WHERE ub.user.age BETWEEN :minAge AND :maxAge AND ub.user.gender = :gender AND ub.book.deletedAt IS NULL "
		+ "GROUP BY ub.book "
		+ "ORDER BY COUNT(ub.book) DESC")
	Page<Book> findTopBooksByAgeAndGender(
		@Param("minAge") int minAge,
		@Param("maxAge") int maxAge,
		@Param("gender") Gender gender,
		Pageable pageable
	);
}
