package com.example.heapreader.domain.book.dto.response;

import com.example.heapreader.domain.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String subtitle;
    private String author;
    private String publisher;
    private LocalDate datePublished;
    private Integer totalPages;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BooksResponseDto(Book book) {
        this.id = book.getId();
        this.genre = book.getGenre().name();
        this.title = book.getTitle();
        this.subtitle = book.getSubtitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.totalPages = book.getTotalPages();
        this.coverImage = book.getCoverImage();
        this.createdAt = book.getCreatedAt();
        this.updatedAt = book.getUpdatedAt();
    }

}
