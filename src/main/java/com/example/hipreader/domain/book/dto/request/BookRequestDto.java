package com.example.hipreader.domain.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {
	private String title;
	private String author;
	private String isbn13;
	private String publisher;
	private LocalDate datePublished;
	private Integer totalPages;
	private String coverImage;
	private String categoryName;
}
