package com.example.hipreader.domain.book.entity;

import com.example.hipreader.common.entity.TimeStamped;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book extends TimeStamped {
	//제목,작가,출판사,장르,총페이지수,이미지
	@Id	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<Author> authors = new ArrayList<>();

	@Column(nullable = false, unique = true)
	private String isbn13;

	@Column(nullable = false)
	private String publisher;

	@Column(nullable = false)
	private LocalDate datePublished;

	private Integer totalPages;

	@Column
	private String coverImage;

	@Column
	private String categoryName;

	public void addAuthors(List<Author> authors) {
		this.authors.addAll(authors);
	}
}

