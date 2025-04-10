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
	private String genre;
	private String title;
	private String author;
	private String publisher;
	private LocalDate datePublished;
	private Integer totalPages;
	private String coverImage;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public BooksResponseDto(Book book) {
		this.id = book.getId();
		this.genre = (book.getGenre() != null) ? book.getGenre().name() : null;
		this.title = book.getTitle();
		this.author = book.getAuthor();
		this.publisher = book.getPublisher();
		this.totalPages = book.getTotalPages();
		this.coverImage = book.getCoverImage();
		this.createdAt = book.getCreatedAt();
		this.updatedAt = book.getUpdatedAt();
	}
}
