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

    List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);

    if (!books.isEmpty()) {
      log.info("DB 검색 결과 있음 - keyword: {}", keyword);
      saveSearchLog(keyword, true);
      return books.stream().map(BooksResponseDto::new).toList();
    }

    log.info("DB에 없음 → 알라딘 API로 검색 - keyword: {}", keyword);
    List<AladinBookDto> aladinResults = aladinService.searchBooks(keyword);
    List<BooksResponseDto> savedBooks = booksService.saveBooksFromAladin(aladinResults);

    saveSearchLog(keyword, !savedBooks.isEmpty());
    return savedBooks;
  }

  private void saveSearchLog(String keyword, boolean resultFound) {
    log.info("검색 로그 저장 - keyword: {}, result: {}", keyword, resultFound);
    // 나중에 Elasticsearch 저장용으로 확장 가능
  }
}

