package com.example.hipreader.domain.bookscore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;

public interface YearlyTopBookRepository extends JpaRepository<YearlyTopBook, Long> {
	@Query("SELECT y FROM YearlyTopBook y WHERE y.year = :year ORDER BY y.totalScore DESC")
	List<YearlyTopBook> findByYear(@Param("year") int year);

	@Modifying
	@Query("DELETE FROM YearlyTopBook y WHERE y.year = :year")
	void deleteAllByYear(@Param("year") int year);
}
