package com.example.heapreader.domain.book.service;

import com.example.heapreader.domain.book.dto.request.BooksRequestDto;
import com.example.heapreader.domain.book.dto.response.BooksResponseDto;
import com.example.heapreader.domain.book.entity.Book;
import com.example.heapreader.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

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
                () -> new IllegalArgumentException("Not found book with id: " + id)
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
                () -> new IllegalArgumentException("Not found book with id: " + id)
        );

        return new BooksResponseDto(book);
    }

    @Transactional(readOnly = true)
    public List<BooksResponseDto> findAllBooks() {
        List<Book> books = bookRepository.findAll();

        List<BooksResponseDto> booksResponseDtos = new ArrayList<>();
        for (Book book : books) {
            booksResponseDtos.add(new BooksResponseDto(book));
        }

        return booksResponseDtos;
    }

    @Transactional
    public void deleteBook(@PathVariable Long id) {

        bookRepository.deleteById(id);
    }
}
