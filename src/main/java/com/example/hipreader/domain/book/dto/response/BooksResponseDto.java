package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.entity.Book;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BooksResponseDto {
	private Long id;
	private String categoryName;
	private String title;
	private String isbn13;
	private String author;
	private String publisher;
	private LocalDate datePublished;
	private Integer totalPages;
	private String coverImage;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public BooksResponseDto(Book book) {
		this.id = book.getId();
		this.categoryName = (book.getCategoryName() != null) ? book.getCategoryName() : null;
		this.title = book.getTitle();
		this.isbn13 = book.getIsbn13();
		this.author = book.getAuthor();
		this.publisher = book.getPublisher();
		this.totalPages = book.getTotalPages();
		this.coverImage = book.getCoverImage();
		this.createdAt = book.getCreatedAt();
		this.updatedAt = book.getUpdatedAt();
		this.datePublished = book.getDatePublished();
	}
}
