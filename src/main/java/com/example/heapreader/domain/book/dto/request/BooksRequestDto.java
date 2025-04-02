package com.example.heapreader.domain.book.dto.request;

import com.example.heapreader.domain.book.entity.Book;
import com.example.heapreader.domain.book.genre.Genre;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BooksRequestDto {

    private Genre genre;
    private String title;
    private String subtitle;
    private String author;
    private String publisher;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate datePublished;
    private Integer totalPages;
    private String coverImage;

}
