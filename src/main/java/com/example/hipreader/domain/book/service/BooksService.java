package com.example.hipreader.domain.book.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.dto.request.BooksRequestDto;
import com.example.hipreader.domain.book.dto.response.BooksResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BooksService {

	private final AladinService aladinService;
	private final BookRepository bookRepository;

	@Transactional
	public BooksResponseDto registerBook(BooksRequestDto dto) {
		Book book = Book.builder()
			.genre(dto.getGenre())
			.title(dto.getTitle())
			.author(dto.getAuthor())
			.publisher(dto.getPublisher())
			.datePublished(dto.getDatePublished())
			.totalPages(dto.getTotalPages())
			.coverImage(dto.getCoverImage())
			.build();
		Book register = bookRepository.save(book);
		return new BooksResponseDto(register);
	}

	@Transactional
	public BooksResponseDto updateBook(Long id, BooksRequestDto dto) {
		Book book = bookRepository.findById(id).orElseThrow(
			() -> new NotFoundException(BOOK_NOT_FOUND)
		);

		book.updateBook(
			dto.getGenre(),
			dto.getTitle(),
			dto.getAuthor(),
			dto.getPublisher(),
			dto.getDatePublished(),
			dto.getTotalPages(),
			dto.getCoverImage()
		);

		return new BooksResponseDto(book);
	}

	@Transactional(readOnly = true)
	public BooksResponseDto findBook(@PathVariable Long id) {
		Book book = bookRepository.findById(id).orElseThrow(
			() -> new NotFoundException(BOOK_NOT_FOUND)
		);

		return new BooksResponseDto(book);
	}

	@Transactional(readOnly = true)
	public PageResponseDto<BooksResponseDto> findAllBooks(Pageable pageable) {

		Page<Book> books = bookRepository.findAll(pageable);
		Page<BooksResponseDto> booksResponseDtos = books.map(BooksResponseDto::new);

		return new PageResponseDto<>(booksResponseDtos);
	}

	@Transactional
	public void deleteBook(@PathVariable Long id) {

		bookRepository.deleteById(id);
	}

	@Transactional
	public List<BooksResponseDto> saveBooksFromAladin(List<AladinBookDto> dtoList) {
		List<BooksResponseDto> result = new ArrayList<>();

		for (AladinBookDto dto : dtoList) {
			if (dto.getItemPage() == null) {
				Integer lookedUpPage = aladinService.fetchItemPageFromItemLookUp(dto.getIsbn13());
				dto.setItemPage(lookedUpPage);
			}

			if (!bookRepository.existsByIsbn13(dto.getIsbn13())) {
				Book book = Book.builder()
					.title(dto.getTitle())
					.author(dto.getAuthor())
					.isbn13(dto.getIsbn13())
					.publisher(dto.getPublisher())
					.datePublished(parsePubDate(dto.getPubDate()))
					.totalPages(dto.getItemPage())
					.coverImage(dto.getCover())
					.genre(null)
					.build();
				Book saved = bookRepository.save(book);
				result.add(new BooksResponseDto(saved));
			}
		}

		return result;
	}

	private LocalDate parsePubDate(String pubDateStr) {
		try {
			if (pubDateStr.length() == 10) {
				return LocalDate.parse(pubDateStr); // yyyy-MM-dd
			} else if (pubDateStr.length() == 7) {
				return LocalDate.parse(pubDateStr + "-01"); // yyyy-MM → yyyy-MM-01
			}
		} catch (Exception e) {
			// 파싱 실패하면 현재 날짜로 fallback
		}
		return LocalDate.now();
	}
}
