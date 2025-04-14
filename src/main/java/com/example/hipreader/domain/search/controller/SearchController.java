package com.example.hipreader.domain.search.controller;

import com.example.hipreader.domain.book.dto.response.BooksResponseDto;
import com.example.hipreader.domain.search.service.SearchService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

  private final SearchService searchService;

  @GetMapping
  public ResponseEntity<List<BooksResponseDto>> searchBooks(@RequestParam String keyword) {
    if(keyword == null || keyword.trim().isEmpty()) {
      return ResponseEntity.badRequest().body(Collections.emptyList());
    }
    List<BooksResponseDto> results = searchService.searchBooks(keyword);
    return ResponseEntity.ok(results);
  }
}
