package com.example.hipreader.domain.bookscore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hipreader.domain.bookscore.entity.YearlyBookScore;

public interface YearlyBookScoreRepository extends JpaRepository<YearlyBookScore, Long> {
	@Query("SELECT y FROM YearlyBookScore y JOIN FETCH y.book WHERE y.year = :year")
	Optional<YearlyBookScore> findByYear(int year);
}
