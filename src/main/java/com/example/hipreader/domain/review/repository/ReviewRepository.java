package com.example.hipreader.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hipreader.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByBook_id(Long bookId);

	Optional<Review> findByIdAndBook_id(Long reviewId, Long bookId);

	boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
