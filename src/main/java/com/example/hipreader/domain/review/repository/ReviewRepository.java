package com.example.hipreader.domain.review.repository;

import com.example.hipreader.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
