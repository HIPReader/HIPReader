package com.example.hipreader.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hipreader.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Optional<Review> findByIdAndBook_id(Long reviewId, Long bookId);

	boolean existsByUserIdAndBookId(Long userId, Long bookId);

	@Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.book WHERE r.book.id = :bookId")
	Page<Review> findByBookIdWithUserAndBook(@Param("bookId") Long bookId, Pageable pageable);
}
