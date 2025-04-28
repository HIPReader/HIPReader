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
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.userbook.document.UserBookDocument;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;
import com.example.hipreader.domain.userbook.service.UserBookSearchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookRecommendService {

	private final UserBookRepository userBookRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserBookSearchService userBookSearchService;

	// 연령별, 성별별, 카테고리별 책 추천 ( MySQL )
	public PageResponseDto<BookRecommendResponseDto> recommendBooksWithoutRedis(Integer age, Gender gender,
		String categoryName,
		Pageable pageable) {
		Page<Book> bookPage = userBookRepository.findRecommendedBooks(age, gender, categoryName, pageable);

		List<BookRecommendResponseDto> content = bookPage.getContent().stream()
			.map(BookRecommendResponseDto::toDto).toList();

		return PageResponseDto.<BookRecommendResponseDto>builder()
			.pageNumber(bookPage.getNumber())
			.pageSize(bookPage.getSize())
			.totalPages(bookPage.getTotalPages())
			.totalElements(bookPage.getTotalElements())
			.content(content)
			.build();
	}

	// 연령별, 성별별, 카테고리별 책 추천 ( Redis )
	public PageResponseDto<BookRecommendResponseDto> recommendBooksWithRedis(Integer age, Gender gender,
		String categoryName,
		Pageable pageable) {

		String key = String.format("recommend:age=%d:gender=%s:categoryName=%s:page=%d:size=%d", age, gender,
			categoryName,
			pageable.getPageNumber(), pageable.getPageSize());

		// Redis 에서 먼저 캐시 조회
		Object cached = redisTemplate.opsForValue().get(key);
		if (cached instanceof PageResponseDto<?>) {
			return (PageResponseDto<BookRecommendResponseDto>)cached;
		}

		Page<Book> bookPage = userBookRepository.findRecommendedBooks(age, gender, categoryName, pageable);

		List<BookRecommendResponseDto> content = bookPage.getContent().stream()
			.map(BookRecommendResponseDto::toDto).toList();

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

	// 연령별, 성별별, 카테고리별 책 추천 ( Redis + Elasticsearch )
	public PageResponseDto<BookRecommendResponseDto> recommendBooksWithRedisAndEs(Integer age, Gender gender,
		String categoryName, Pageable pageable) {

		String key = String.format("recommend:age=%d:gender=%s:categoryName=%s:page=%d:size=%d", age, gender,
			categoryName,
			pageable.getPageNumber(), pageable.getPageSize());

		// Redis 에서 먼저 캐시 조회
		Object cached = redisTemplate.opsForValue().get(key);
		if (cached instanceof PageResponseDto<?>) {
			return (PageResponseDto<BookRecommendResponseDto>)cached;
		}

		// 캐시에 데이터 없을 경우 Elasticsearch 조회
		Integer ageGroup = (age != null) ? (age / 10) * 10 : null;

		Page<UserBookDocument> bookDocumentPage = userBookSearchService.searchByCondition(ageGroup, gender,
			categoryName, pageable);

		List<BookRecommendResponseDto> content = bookDocumentPage.getContent().stream()
			.map(BookRecommendResponseDto::toDto).toList();

		PageResponseDto<BookRecommendResponseDto> response = PageResponseDto.<BookRecommendResponseDto>builder()
			.pageNumber(bookDocumentPage.getNumber())
			.pageSize(bookDocumentPage.getSize())
			.totalPages(bookDocumentPage.getTotalPages())
			.totalElements(bookDocumentPage.getTotalElements())
			.content(content)
			.build();

		// 캐시에 키가 없을 경우, Redis 에 저장
		if (!response.getContent().isEmpty()) {
			redisTemplate.opsForValue().set(key, response);
			redisTemplate.expire(key, Duration.ofMinutes(10));
		}

		return response;
	}
}
