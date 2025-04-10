package com.example.hipreader.domain.userbook.dto.response;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userbook.status.Status;

import lombok.Getter;

@Getter
public class RegisterUSerBookResponseDto {
	private final String username;
	private final String title;
	private final String author;
	private final double percentage;
	private final Status status;

	public RegisterUSerBookResponseDto(User user, Book book, int progress, Status status) {
		this.username = user.getNickname();
		this.title = book.getTitle();
		this.author = book.getAuthor();
		this.percentage = Math.round((progress / (double)book.getTotalPages()) * 100.0);
		this.status = status;
	}


}
