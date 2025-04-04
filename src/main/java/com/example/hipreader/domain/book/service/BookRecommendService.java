package com.example.hipreader.domain.book.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.response.BookRecommendGetResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.user.age.AgeGroup;
import com.example.hipreader.domain.user.gender.Gender;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookRecommendService {

	private final UserBookRepository userBookRepository;

	// 연령별, 성별별 책 추천
	public PageResponseDto<BookRecommendGetResponseDto> getPopularBooksByAgeAndGender(int page, int size, int age,
		Gender gender) {
		PageRequest pageRequest = PageRequest.of(page, size);

		// 연령대 변환
		AgeGroup ageGroup = AgeGroup.fromAge(age);
		Page<Book> bookPage = userBookRepository.findTopBooksByAgeAndGender(
			ageGroup.getMinAge(),
			ageGroup.getMaxAge(),
			gender,
			pageRequest
		);

		List<BookRecommendGetResponseDto> content = bookPage.getContent().stream()
			.map(book -> new BookRecommendGetResponseDto(
				book.getTitle(),
				book.getSubtitle(),
				book.getAuthor(),
				book.getPublisher(),
				book.getCoverImage(),
				book.getGenre()
			)).toList();

		return PageResponseDto.<BookRecommendGetResponseDto>builder()
			.pageNumber(bookPage.getNumber())
			.pageSize(bookPage.getSize())
			.totalPages(bookPage.getTotalPages())
			.totalElements(bookPage.getTotalElements())
			.content(content)
			.build();
	}
}
