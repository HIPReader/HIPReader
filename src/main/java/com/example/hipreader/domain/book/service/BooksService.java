package com.example.hipreader.domain.book.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import com.example.hipreader.common.dto.response.PageResponseDto;
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

@Service
@RequiredArgsConstructor
public class BooksService {

    private final BookRepository bookRepository;

    @Transactional
    public BooksResponseDto registerBook(BooksRequestDto dto) {
        Book book = Book.builder()
                .genre(dto.getGenre())
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
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
                dto.getSubtitle(),
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
}
