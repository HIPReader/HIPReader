package com.example.hipreader.domain.book.entity;

import com.example.hipreader.common.entity.TimeStamped;

import com.example.hipreader.domain.book.dto.request.BookRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
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

	public void patchBook(BookRequestDto dto) {
		if (dto.getTitle() != null) this.title = dto.getTitle();
		if (dto.getIsbn13() != null) this.isbn13 = dto.getIsbn13();
		if (dto.getPublisher() != null) this.publisher = dto.getPublisher();
		if (dto.getDatePublished() != null) this.datePublished = dto.getDatePublished();
		if (dto.getTotalPages() != null) this.totalPages = dto.getTotalPages();
		if (dto.getCoverImage() != null) this.coverImage = dto.getCoverImage();
		if (dto.getCategoryName() != null) this.categoryName = dto.getCategoryName();

		if (dto.getAuthor() != null) {
			this.authors.clear();
			List<Author> updatedAuthors = Arrays.stream(dto.getAuthor().split(","))
					.map(String::trim)
					.map(name -> name.replaceAll("\\s*\\(.*?\\)", ""))
					.map(name -> Author.builder().name(name).book(this).build())
					.toList();

			this.addAuthors(updatedAuthors);
		}
	}
}

