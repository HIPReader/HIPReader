package com.example.hipreader.domain.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Optional<Review> findByIdAndBook_id(Long reviewId, Long bookId);

	boolean existsByUserIdAndBookId(Long userId, Long bookId);

	Page<Review> findAllByBookId(Long bookId, Pageable pageable);
}
