package com.example.hipreader.domain.userbook.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AverageStaticResponseDto {
  private final double averageReadingDays;
  private final double averagePagesPerBook;
  private final long totalFinishedBooks;
}
