package com.example.hipreader.domain.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AladinBookDto {
	private String title;
	private String author;
	private String isbn13;
	private String publisher;
	private String pubDate;
	private String cover;
	private Integer itemPage;
	private String categoryName;
}
