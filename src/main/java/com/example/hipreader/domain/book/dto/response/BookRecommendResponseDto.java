package com.example.hipreader.domain.book.dto.response;

import com.example.hipreader.domain.book.genre.Genre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRecommendResponseDto {
	private String title;
	private String subtitle;
	private String author;
	private String publisher;
	private String coverImage;
	private Genre genre;
}
