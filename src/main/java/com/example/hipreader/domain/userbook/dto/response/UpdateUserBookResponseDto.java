package com.example.hipreader.domain.userbook.dto.response;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.status.Status;

import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateUserBookResponseDto {
	private final String username;
	private final String title;
	private final String author;
	private final double percentage;
	private final Status status;

	public static UpdateUserBookResponseDto toDto(UserBook userBook) {
		Book book = userBook.getBook();
		String authorNames = book.getAuthors().stream()
				.map(Author::getName)
				.collect(Collectors.joining(", "));

		return UpdateUserBookResponseDto.builder()
				.username(userBook.getUser().getNickname())
				.title(book.getTitle())
				.author(authorNames)
				.percentage(calculatePercentage(userBook))
				.status(userBook.getStatus())
				.build();
	}

	private static double calculatePercentage(UserBook userBook) {
		if (userBook.getBook().getTotalPages() == 0) return 0.0;
		return Math.round(
			(userBook.getProgress() / (double) userBook.getBook().getTotalPages()) * 100.0
		);
	}
}
