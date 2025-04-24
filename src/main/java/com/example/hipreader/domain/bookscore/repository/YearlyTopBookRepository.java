package com.example.hipreader.domain.bookscore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;

public interface YearlyTopBookRepository extends JpaRepository<YearlyTopBook, Long> {
	@Query("SELECT y FROM YearlyTopBook y WHERE y.year = :year ORDER BY y.totalScore DESC")
	List<YearlyTopBook> findByYear(int year);
}
