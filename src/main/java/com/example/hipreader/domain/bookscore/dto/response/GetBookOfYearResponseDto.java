package com.example.hipreader.domain.bookscore.dto.response;

import com.example.hipreader.domain.book.entity.Book;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetBookOfYearResponseDto {

	private String title;
	private String author;
	private String publisher;

	@Min(0)
	private final long totalScore;

	public static GetBookOfYearResponseDto from(Book book, long totalScore) {
		return builder()
			.title(book.getTitle())
			.author(book.getAuthor())
			.publisher(book.getPublisher())
			.totalScore(totalScore)
			.build();
	}
}
