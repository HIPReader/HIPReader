package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.userbook.document.UserBookDocument;

import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRecommendResponseDto {
	private Long bookId;
	private String title;
	private String author;
	private String publisher;
	private String coverImage;
	private String categoryName;

	public static BookRecommendResponseDto toDto(Book book) {

		String authors = book.getAuthors().stream()
			.map(Author::getName)
			.collect(Collectors.joining(", "));

		return BookRecommendResponseDto.builder()
			.bookId(book.getId())
			.title(book.getTitle())
			.author(authors)
			.publisher(book.getPublisher())
			.coverImage(book.getCoverImage())
			.categoryName(book.getCategoryName())
			.build();
	}

	public static BookRecommendResponseDto toDto(UserBookDocument userBookDocument) {
		return BookRecommendResponseDto.builder()
			.bookId(userBookDocument.getBookId())
			.title(userBookDocument.getTitle())
			.author(userBookDocument.getAuthor())
			.publisher(userBookDocument.getPublisher())
			.coverImage(userBookDocument.getCoverImage())
			.categoryName(userBookDocument.getCategoryName())
			.build();
	}
}
