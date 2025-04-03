package com.example.hipreader.domain.userbook.dto.response;

import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.userbook.status.Status;
import com.example.hipreader.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserBookResponseDto {
    private final String username;
    private final String title;
    private final String author;
    private final double percentage;
    private final Status status;

    public UserBookResponseDto(User user, Book book, int progress, Status status) {
        this.username = user.getNickname();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.percentage = Math.round((progress / (double) book.getTotalPages()) * 100.0);
        this.status = status;
    }
}
