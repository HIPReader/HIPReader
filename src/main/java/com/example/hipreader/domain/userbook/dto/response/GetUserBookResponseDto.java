package com.example.hipreader.domain.userbook.dto.response;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.status.Status;
import com.example.hipreader.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserBookResponseDto {
	private final String username;
	private final String title;
	private final String author;
	private final double percentage;
	private final Status status;

	public static GetUserBookResponseDto toDto(UserBook userBook) {
		return GetUserBookResponseDto.builder()
			.username(userBook.getUser().getNickname())
			.title(userBook.getBook().getTitle())
			.author(userBook.getBook().getAuthor())
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
