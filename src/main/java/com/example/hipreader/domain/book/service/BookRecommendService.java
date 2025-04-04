package com.example.hipreader.domain.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.response.BookRecommendResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.genre.Genre;
import com.example.hipreader.domain.user.gender.Gender;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookRecommendService {

	private final UserBookRepository userBookRepository;

	// 연령별, 성별별 책 추천
	public PageResponseDto<BookRecommendResponseDto> recommendBooks(Integer age, Gender gender, Genre genre,
		Pageable pageable) {
		Page<Book> bookPage = userBookRepository.findRecommendedBooks(age, gender, genre, pageable);

		List<BookRecommendResponseDto> content = bookPage.getContent().stream()
			.map(book -> new BookRecommendResponseDto(
				book.getTitle(),
				book.getSubtitle(),
				book.getAuthor(),
				book.getPublisher(),
				book.getCoverImage(),
				book.getGenre()
			)).toList();

		return PageResponseDto.<BookRecommendResponseDto>builder()
			.pageNumber(bookPage.getNumber())
			.pageSize(bookPage.getSize())
			.totalPages(bookPage.getTotalPages())
			.totalElements(bookPage.getTotalElements())
			.content(content)
			.build();
	}
}
