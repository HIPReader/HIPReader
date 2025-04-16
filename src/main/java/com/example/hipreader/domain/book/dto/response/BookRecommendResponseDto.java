package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.genre.Genre;
import com.example.hipreader.domain.userbook.document.UserBookDocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRecommendResponseDto {
	private String title;
	private String author;
	private String publisher;
	private String coverImage;
	private String categoryName;

	public static BookRecommendResponseDto toDto(Book book) {
		return BookRecommendResponseDto.builder()
			.title(book.getTitle())
			.author(book.getAuthor())
			.publisher(book.getPublisher())
			.coverImage(book.getCoverImage())
			.categoryName(book.getCategoryName())
			.build();
	}

	public static BookRecommendResponseDto toDto(UserBookDocument userBookDocument) {
		return BookRecommendResponseDto.builder()
			.title(userBookDocument.getTitle())
			.author(userBookDocument.getAuthor())
			.publisher(userBookDocument.getPublisher())
			.coverImage(userBookDocument.getCoverImage())
			.categoryName(userBookDocument.getCategoryName())
			.build();
	}
}
