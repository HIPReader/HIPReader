package com.example.hipreader.domain.book.genre;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Genre {

  GENERAL("총류"),         // 000
  PHILOSOPHY("철학"),            // 100
  RELIGION("종교"),              // 200
  SOCIAL_SCIENCE("사회과학"),     // 300
  NATURAL_SCIENCE("자연과학"),   // 400
  TECHNOLOGY("기술과학"),         // 500
  ARTS("예술"),                  // 600
  LANGUAGE("언어"),              // 700
  LITERATURE("문학"),            // 800
  HISTORY("역사"),              // 900
  ETC("기타");

  private final String genreName;

  @Override
  public String toString() {
    return genreName;
  }
}