package com.example.hipreader.domain.review.repository;

import com.example.hipreader.domain.review.entity.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findAllByBook_id(Long bookId);

	Optional<Review> findByIdAndBook_id(Long reviewId, Long bookId);
}
