package com.example.hipreader.domain.bookscore.entity;

import com.example.hipreader.domain.book.entity.Book;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "yearly_top_books",
	indexes = @Index(name = "idx_year_score", columnList = "year, totalScore DESC")  // ← 핵심 변경점
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlyTopBook {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private int year;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(nullable = false)
	private long totalScore;
}
