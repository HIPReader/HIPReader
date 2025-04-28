package com.example.hipreader.domain.bookscore.dto.response;

import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public record GetBookOfYearResponseDto(
	int year,
	List<BookInfo> topBooks,
	long maxScore
) {
	public record BookInfo(
		String title,
		String author,
		String publisher,
		long totalScore
	) {}

	public static GetBookOfYearResponseDto from(int year, List<YearlyTopBook> yearlyTopBooks) {
		List<BookInfo> bookInfos = yearlyTopBooks.stream()
			.map(ytb -> convertToBookInfo(ytb.getBook(), ytb.getTotalScore()))
			.collect(Collectors.toList());

		// DB에서 이미 정렬된 데이터를 받으므로 0번 인덱스가 최대값
		long max = yearlyTopBooks.isEmpty() ? 0 : yearlyTopBooks.get(0).getTotalScore();

		return new GetBookOfYearResponseDto(year, bookInfos, max);
	}

	private static BookInfo convertToBookInfo(Book book, long score) {
		String authors = book.getAuthors().stream()
			.map(Author::getName)
			.collect(Collectors.joining(", "));

		return new BookInfo(
			book.getTitle(),
			authors,
			book.getPublisher(),
			score
		);
	}
}
