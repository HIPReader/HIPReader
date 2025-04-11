package com.example.hipreader.domain.book.dto.request;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.genre.Genre;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BooksRequestDto {
	private String title;
	private String author;
	private String isbn13;
	private String publisher;
	private LocalDate datePublished;
	private Integer totalPages;
	private String coverImage;
	private Genre genre;

	// 변경 사항이 없으면 그대로 저장.
	public void applyIfChanged(BooksRequestDto dto, Book book) {
		if (dto.getGenre() != null && !dto.getGenre().equals(book.getGenre())) {
			book.setGenre(dto.getGenre());
		}

		if (dto.isbn13 != null && !dto.getIsbn13().equals(book.getIsbn13())) {
			book.setIsbn13(dto.getIsbn13());
		}

		if (dto.getTitle() != null && !dto.getTitle().equals(book.getTitle())) {
			book.setTitle(dto.getTitle());
		}

		if (dto.getAuthor() != null && !dto.getAuthor().equals(book.getAuthor())) {
			book.setAuthor(dto.getAuthor());
		}
		if (dto.getPublisher() != null && !dto.getPublisher().equals(book.getPublisher())) {
			book.setPublisher(dto.getPublisher());
		}
		if (dto.getDatePublished() != null && !dto.getDatePublished().equals(book.getDatePublished())) {
			book.setDatePublished(dto.getDatePublished());
		}
		if (dto.getTotalPages() != null && !dto.getTotalPages().equals(book.getTotalPages())) {
			book.setTotalPages(dto.getTotalPages());
		}
		if (dto.getCoverImage() != null && !Objects.equals(dto.getCoverImage(), book.getCoverImage())) {
			book.setCoverImage(dto.getCoverImage());
		}
	}
}
