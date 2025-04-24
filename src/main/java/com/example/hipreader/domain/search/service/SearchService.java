package com.example.hipreader.domain.search.service;

import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.example.hipreader.domain.book.dto.response.BookResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.book.service.AladinService;
import com.example.hipreader.domain.book.service.BookService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final BookRepository bookRepository;
  private final AladinService aladinService;
  private final BookService bookService;

  public List<BookResponseDto> searchBooks(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Collections.emptyList();
    }

    String cleanedKeyword = keyword.replaceAll("\\s+", "").toLowerCase();
    List<Book> books = bookRepository.findByTitleIgnoringSpaces(cleanedKeyword);

    if (!books.isEmpty()) {
      return books.stream().map(BookResponseDto::toDto).toList();
    }

    log.info("DB에 없음 → 알라딘 API로 검색 - keyword: {}", keyword);
    List<AladinBookDto> aladinResults = aladinService.searchBooks(keyword);
    List<BookResponseDto> savedBooks = bookService.saveBooksFromAladin(aladinResults);

    return savedBooks;
  }
}

