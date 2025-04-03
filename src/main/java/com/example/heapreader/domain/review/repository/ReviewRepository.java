package com.example.heapreader.domain.review.repository;

import com.example.heapreader.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
