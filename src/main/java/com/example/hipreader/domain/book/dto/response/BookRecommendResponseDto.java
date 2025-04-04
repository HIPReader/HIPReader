package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.genre.Genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookRecommendResponseDto {
	private final String title;
	private final String subtitle;
	private final String author;
	private final String publisher;
	private final String coverImage;
	private final Genre genre;
}
