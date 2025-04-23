package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchBookResponseDto {
  private Long id;
  private String categoryName;
  private String title;
  private String isbn13;
  private String author;
  private String publisher;
  private LocalDate datePublished;
  private Integer totalPages;
  private String coverImage;
  private LocalDateTime updatedAt;

  public static PatchBookResponseDto toDto (Book book) {
    String authors = book.getAuthors().stream().map(Author::toString).collect(Collectors.joining(","));

    return PatchBookResponseDto.builder()
        .id(book.getId())
        .title(book.getTitle())
        .isbn13(book.getIsbn13())
        .author(authors)
        .publisher(book.getPublisher())
        .totalPages(book.getTotalPages())
        .coverImage(book.getCoverImage())
        .categoryName(book.getCategoryName())
        .updatedAt(book.getUpdatedAt())
        .datePublished(book.getDatePublished())
        .build();
  }
}
