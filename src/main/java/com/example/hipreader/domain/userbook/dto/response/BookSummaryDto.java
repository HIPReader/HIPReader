package com.example.hipreader.domain.userbook.dto.response;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookSummaryDto {
  private final String title;
  private final String authors;
  private final Integer totalPages;

  public static BookSummaryDto toDto(Book book) {
    return BookSummaryDto.builder()
        .title(book.getTitle())
        .authors(book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", ")))
        .totalPages(book.getTotalPages())
        .build();
  }
}