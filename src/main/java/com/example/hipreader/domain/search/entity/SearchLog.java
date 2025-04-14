package com.example.hipreader.domain.search.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "search_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String keyword;

  private boolean fromDb;  // true면 DB 검색 결과, false면 Open API

  private boolean success; // 검색 결과가 있었는지

  private LocalDateTime searchedAt;
}

