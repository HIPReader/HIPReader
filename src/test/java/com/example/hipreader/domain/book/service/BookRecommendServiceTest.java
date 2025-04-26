package com.example.hipreader.domain.book.service;

import static java.time.LocalTime.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.common.filter.JwtAuthenticationFilter;
import com.example.hipreader.common.util.JwtUtil;
import com.example.hipreader.domain.book.dto.response.BookRecommendResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.userbook.document.UserBookDocument;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;
import com.example.hipreader.domain.userbook.service.UserBookSearchService;

@ExtendWith(MockitoExtension.class)
class BookRecommendServiceTest {

	@Mock
	UserBookRepository userBookRepository;

	@Mock
	RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@Mock
	UserBookSearchService userBookSearchService;

	@InjectMocks
	BookRecommendService bookRecommendService;

	@Test
	void 연령별_성별별_카테고리별_책추천_성공_without_Redis() {
		// given
		Integer age = 20;
		Gender gender = Gender.FEMALE;
		String categoryName = "소설";
		Pageable pageable = PageRequest.of(0, 10);

		List<Book> books = Arrays.asList(
			new Book(1L, "타이틀1", "12345", "출판사1", LocalDate.now(), 100, null, "소설"),
			new Book(2L, "타이틀2", "12345", "출판사2", LocalDate.now(), 100, null, "소설"),
			new Book(3L, "타이틀3", "12345", "출판사3", LocalDate.now(), 100, null, "소설")
		);

		Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

		given(userBookRepository.findRecommendedBooks(age, gender, categoryName, pageable)).willReturn(bookPage);

		// when
		PageResponseDto<BookRecommendResponseDto> pageResponseDto = bookRecommendService.recommendBooksWithoutRedis(
			age, gender, categoryName, pageable);

		// then
		assertThat(pageResponseDto.getPageNumber()).isEqualTo(0);
		assertThat(pageResponseDto.getPageSize()).isEqualTo(10);
		assertThat(pageResponseDto.getTotalPages()).isEqualTo(1);
		assertThat(pageResponseDto.getTotalElements()).isEqualTo(3);
		assertThat(pageResponseDto.getContent().get(0).getTitle()).isEqualTo("타이틀1");
	}

	@Test
	void 연령별_성별별_카테고리별_책추천_성공_with_Redis() {
		// given
		Integer age = 20;
		Gender gender = Gender.FEMALE;
		String categoryName = "소설";
		Pageable pageable = PageRequest.of(0, 10);

		String key = String.format("recommend:age=%d:gender=%s:categoryName=%s:page=%d:size=%d", age, gender,
			categoryName,
			pageable.getPageNumber(), pageable.getPageSize());
		Object fakeCached = new Object();

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(key)).thenReturn(fakeCached);

		List<Book> books = Arrays.asList(
			new Book(1L, "타이틀1", "12345", "출판사1", LocalDate.now(), 100, null, "소설"),
			new Book(2L, "타이틀2", "12345", "출판사2", LocalDate.now(), 100, null, "소설"),
			new Book(3L, "타이틀3", "12345", "출판사3", LocalDate.now(), 100, null, "소설")
		);

		Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

		given(userBookRepository.findRecommendedBooks(age, gender, categoryName, pageable)).willReturn(bookPage);

		// when
		PageResponseDto<BookRecommendResponseDto> pageResponseDto = bookRecommendService.recommendBooksWithRedis(
			age, gender, categoryName, pageable);

		// then
		assertThat(pageResponseDto.getPageNumber()).isEqualTo(0);
		assertThat(pageResponseDto.getPageSize()).isEqualTo(10);
		assertThat(pageResponseDto.getTotalPages()).isEqualTo(1);
		assertThat(pageResponseDto.getTotalElements()).isEqualTo(3);
		assertThat(pageResponseDto.getContent().get(0).getTitle()).isEqualTo("타이틀1");
	}

	@Test
	void 연령별_성별별_카테고리별_책추천_성공_with_Redis_and_Es() {
		// given
		Integer age = 20;
		Gender gender = Gender.FEMALE;
		String categoryName = "소설";
		Pageable pageable = PageRequest.of(0, 10);
		Integer ageGroup = (age != null) ? (age / 10) * 10 : null;

		String key = String.format("recommend:age=%d:gender=%s:categoryName=%s:page=%d:size=%d", age, gender,
			categoryName,
			pageable.getPageNumber(), pageable.getPageSize());
		Object fakeCached = new Object();

		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.get(key)).thenReturn(fakeCached);

		List<UserBookDocument> bookDocumentList = Arrays.asList(
			new UserBookDocument("1_1", "타이틀1", "작가1", "출판사1", null, "소설", "FEMALE", 10),
			new UserBookDocument("1_2", "타이틀2", "작가2", "출판사2", null, "소설", "FEMALE", 10),
			new UserBookDocument("1_3", "타이틀3", "작가3", "출판사3", null, "소설", "FEMALE", 10)
		);

		Page<UserBookDocument> bookDocumentPage = new PageImpl<>(bookDocumentList, pageable, bookDocumentList.size());

		given(userBookSearchService.searchByCondition(ageGroup, gender, categoryName, pageable)).willReturn(
			bookDocumentPage);

		// when
		PageResponseDto<BookRecommendResponseDto> pageResponseDto = bookRecommendService.recommendBooksWithRedisAndEs(
			age, gender, categoryName, pageable);

		// then
		assertThat(pageResponseDto.getPageNumber()).isEqualTo(0);
		assertThat(pageResponseDto.getPageSize()).isEqualTo(10);
		assertThat(pageResponseDto.getTotalPages()).isEqualTo(1);
		assertThat(pageResponseDto.getTotalElements()).isEqualTo(3);
		assertThat(pageResponseDto.getContent().get(0).getTitle()).isEqualTo("타이틀1");
	}
}