package com.example.hipreader.domain.book.service;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.BadRequestException;
import static com.example.hipreader.common.exception.ErrorCode.*;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.dto.request.BookRequestDto;
import com.example.hipreader.domain.book.dto.response.BookResponseDto;
import com.example.hipreader.domain.book.dto.response.FindBookResponseDto;
import com.example.hipreader.domain.book.dto.response.PatchBookResponseDto;
import com.example.hipreader.domain.book.entity.Author;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;

import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

	private final AladinService aladinService;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	@Transactional
	public BookResponseDto registerBook(AuthUser authUser, BookRequestDto dto) {

		User user = userRepository.findUserById(authUser.getId())
				.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		if (bookRepository.existsByIsbn13(dto.getIsbn13())) {
			throw new BadRequestException(BOOK_DUPLICATION);
		}

		Book book = Book.builder()
			.title(dto.getTitle())
			.isbn13(dto.getIsbn13())
			.publisher(dto.getPublisher())
			.datePublished(dto.getDatePublished())
			.totalPages(dto.getTotalPages())
			.coverImage(dto.getCoverImage())
			.categoryName(dto.getCategoryName())
			.build();

		if(dto.getAuthor() != null && !dto.getAuthor().isBlank()) {
			List<Author> authors = parseAuthors(dto.getAuthor(), book);
			book.addAuthors(authors);
		}

		Book register = bookRepository.save(book);
		return BookResponseDto.toDto(register);
	}

	@Transactional
	public PatchBookResponseDto patchBook(AuthUser authUser, Long bookId, BookRequestDto dto) {

		User user = userRepository.findUserById(authUser.getId())
				.orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

		Book book = bookRepository.findById(bookId).orElseThrow(
			() -> new NotFoundException(BOOK_NOT_FOUND));

		book.patchBook(dto);

		return PatchBookResponseDto.toDto(book);
	}

	@Transactional(readOnly = true)
	public FindBookResponseDto findBook(@PathVariable Long id) {
		Book book = bookRepository.findById(id).orElseThrow(
			() -> new NotFoundException(BOOK_NOT_FOUND)
		);

		return FindBookResponseDto.toDto(book);
	}

	@Transactional(readOnly = true)
	public PageResponseDto<FindBookResponseDto> findAllBooks(Pageable pageable) {

		Page<Book> books = bookRepository.findAll(pageable);
		Page<FindBookResponseDto> booksResponseDtos = books.map(FindBookResponseDto::toDto);

		return new PageResponseDto<>(booksResponseDtos);
	}

	@Transactional
	public void deleteBook(@PathVariable Long id) {

		bookRepository.deleteById(id);
	}

	@Transactional
	public List<BookResponseDto> saveBooksFromAladin(List<AladinBookDto> dtoList) {
		List<BookResponseDto> result = new ArrayList<>();

		for (AladinBookDto dto : dtoList) {
			if (dto.getItemPage() == null) {
				Integer itemPage = aladinService.fetchItemPageFromItemLookUp(dto.getIsbn13());
				dto.setItemPage(itemPage);
			}
			if (dto.getCategoryName() == null) {
				dto.setCategoryName("기타");
			}

			if (!bookRepository.existsByIsbn13(dto.getIsbn13())) {
				Book book = Book.builder()
					.title(dto.getTitle())
					.isbn13(dto.getIsbn13())
					.publisher(dto.getPublisher())
					.datePublished(parsePubDate(dto.getPubDate()))
					.totalPages(dto.getItemPage())
					.coverImage(dto.getCover())
					.categoryName(dto.getCategoryName())
					.build();

				List<Author> authors = parseAuthors(dto.getAuthor(), book);
				book.addAuthors(authors);
				Book saved = bookRepository.save(book);
				result.add(BookResponseDto.toDto(saved));

			}
		}

		return result;
	}

	private List<Author> parseAuthors(String rawAuthors, Book book) {
		if (rawAuthors == null || rawAuthors.isBlank()) return List.of();

		return Arrays.stream(rawAuthors.split(","))
				.map(String::trim)
				.map(name -> name.replaceAll("\\s*\\(.*?\\)", "")) // 괄호 속 역할 제거
				.map(cleanName -> Author.builder()
						.name(cleanName)
						.book(book)
						.build())
				.toList();
	}


	private LocalDate parsePubDate(String pubDateStr) {
		try {
			if (pubDateStr.length() == 10) {
				return LocalDate.parse(pubDateStr); // yyyy-MM-dd
			} else if (pubDateStr.length() == 7) {
				return LocalDate.parse(pubDateStr + "-01"); // yyyy-MM → yyyy-MM-01
			}
		} catch (Exception e) {
			log.warn("parsing pub date has problem");
		}
		return LocalDate.now();
	}
}
