package com.example.hipreader.domain.book.dto.request;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BooksRequestDto {
	private String title;
	private String author;
	private String isbn13;
	private String publisher;
	private LocalDate datePublished;
	private Integer totalPages;
	private String coverImage;
	private String categoryName;

	// 변경 사항이 없으면 그대로 저장.
	public void applyIfChanged(BooksRequestDto dto, Book book) {
		if (dto.getCategoryName() != null && !dto.getCategoryName().equals(book.getCategoryName())) {
			book.setCategoryName(dto.getCategoryName());
		}

		if (dto.getIsbn13() != null && !dto.getIsbn13().equals(book.getIsbn13())) {
			book.setIsbn13(dto.getIsbn13());
		}

		if (dto.getTitle() != null && !dto.getTitle().equals(book.getTitle())) {
			book.setTitle(dto.getTitle());
		}

		// 작가 비교 및 업데이트 (author는 comma-separated string으로 들어옴)
		if (dto.getAuthor() != null) {
			String newAuthorsRaw = dto.getAuthor();
			String existingAuthors = book.getAuthors().stream()
					.map(Author::getName)
					.collect(Collectors.joining(", "));

			if (!existingAuthors.equals(newAuthorsRaw)) {
				// 기존 author 제거
				book.getAuthors().clear();

				// 새 author 리스트로 교체
				List<Author> updatedAuthors = Arrays.stream(newAuthorsRaw.split(","))
						.map(String::trim)
						.map(name -> name.replaceAll("\\s*\\(.*?\\)", "")) // (지은이) 같은 역할 제거
						.map(name -> Author.builder().name(name).book(book).build())
						.toList();

				book.addAuthors(updatedAuthors);
			}
		}

		if (dto.getPublisher() != null && !dto.getPublisher().equals(book.getPublisher())) {
			book.setPublisher(dto.getPublisher());
		}

		if (dto.getDatePublished() != null && !dto.getDatePublished().equals(book.getDatePublished())) {
			book.setDatePublished(dto.getDatePublished());
		}

		if (dto.getTotalPages() != null && !dto.getTotalPages().equals(book.getTotalPages())) {
			book.setTotalPages(dto.getTotalPages());
		}

		if (dto.getCoverImage() != null && !Objects.equals(dto.getCoverImage(), book.getCoverImage())) {
			book.setCoverImage(dto.getCoverImage());
		}
	}

}
