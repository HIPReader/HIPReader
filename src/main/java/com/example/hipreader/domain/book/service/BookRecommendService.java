package com.example.hipreader.domain.book.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
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
	private final RedisTemplate<String, Object> redisTemplate;

	// 연령별, 성별별 책 추천 ( MySQL )
	public PageResponseDto<BookRecommendResponseDto> recommendBooksWithoutRedis(Integer age, Gender gender, Genre genre,
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

	// 연령별, 성별별, 장르별 책 추천 ( Redis )
	public PageResponseDto<BookRecommendResponseDto> recommendBooksWithRedis(Integer age, Gender gender, Genre genre,
		Pageable pageable) {

		String key = String.format("recommend:age=%d:gender=%s:genre=%s:page=%d:size=%d", age, gender, genre.name(),
			pageable.getPageNumber(), pageable.getPageSize());

		// Redis 에서 먼저 캐시 조회
		Object cached = redisTemplate.opsForValue().get(key);
		if (cached instanceof PageResponseDto<?>) {
			return (PageResponseDto<BookRecommendResponseDto>)cached;
		}

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

		PageResponseDto<BookRecommendResponseDto> response = PageResponseDto.<BookRecommendResponseDto>builder()
			.pageNumber(bookPage.getNumber())
			.pageSize(bookPage.getSize())
			.totalPages(bookPage.getTotalPages())
			.totalElements(bookPage.getTotalElements())
			.content(content)
			.build();

		// 캐시에 키가 없을 경우, Redis 에 저장
		redisTemplate.opsForValue().set(key, response);
		redisTemplate.expire(key, Duration.ofMinutes(10));

		return response;
	}
}
