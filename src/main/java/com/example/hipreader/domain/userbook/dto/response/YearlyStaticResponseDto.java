package com.example.hipreader.domain.userbook.dto.response;

import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.userbook.entity.UserBook;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
@AllArgsConstructor
public class YearlyStaticResponseDto {
  private final String username;
  private final long totalBooks;
  private final long totalPages;

  private final Page<BookSummaryDto> books;

  public static YearlyStaticResponseDto toDto(User user, Page<UserBook> finishedBooks, Pageable pageable) {
    long totalBooks = finishedBooks.getTotalElements();
    long totalPages = finishedBooks.stream()
        .map(UserBook::getBook)
        .mapToInt(book -> book.getTotalPages() != null ? book.getTotalPages() : 0)
        .sum();

    Page<BookSummaryDto> pagedBooks = finishedBooks.map(userBook ->
        BookSummaryDto.toDto(userBook.getBook())
    );

    return new YearlyStaticResponseDto(user.getNickname(), totalBooks, totalPages, pagedBooks);
  }
}
