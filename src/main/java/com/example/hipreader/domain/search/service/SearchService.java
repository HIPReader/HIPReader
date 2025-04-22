package com.example.hipreader.domain.search.service;

import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.example.hipreader.domain.book.dto.response.BooksResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.book.service.AladinService;
import com.example.hipreader.domain.book.service.BooksService;
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
  private final BooksService booksService;

  public List<BooksResponseDto> searchBooks(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return Collections.emptyList();
    }

    String cleanedKeyword = keyword.replaceAll("\\s+", "").toLowerCase();
    List<Book> books = bookRepository.findByTitleIgnoringSpaces(cleanedKeyword);

    if (!books.isEmpty()) {
      return books.stream().map(BooksResponseDto::new).toList();
    }

    log.info("DB에 없음 → 알라딘 API로 검색 - keyword: {}", keyword);
    List<AladinBookDto> aladinResults = aladinService.searchBooks(keyword);
    List<BooksResponseDto> savedBooks = booksService.saveBooksFromAladin(aladinResults);

    return savedBooks;
  }
}

