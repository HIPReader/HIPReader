package com.example.heapreader.domain.book.entity;

import com.example.heapreader.domain.book.genre.Genre;
import com.example.heapreader.common.entity.TimeStamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "books")
public class Book extends TimeStamped {
	//제목,작가,출판사,장르,총페이지수,이미지
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String title;
	private String author;
	private String publisher;
	private Integer totalPages;
	private String coverImage;

	@Enumerated(EnumType.STRING)
	private Genre genre;

}
