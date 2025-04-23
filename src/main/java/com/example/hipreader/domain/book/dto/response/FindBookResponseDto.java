package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindBookResponseDto {
  private Long id;
  private String categoryName;
  private String title;
  private String author;
  private String publisher;

  public static FindBookResponseDto toDto (Book book) {
    String authors = book.getAuthors().stream().map(Author::toString).collect(Collectors.joining(","));

    return FindBookResponseDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .author(authors)
        .publisher(book.getPublisher())
        .categoryName(book.getCategoryName())
        .build();
  }
}
