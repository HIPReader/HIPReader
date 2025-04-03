package com.example.hipreader.domain.book.service;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.request.BooksRequestDto;
import com.example.hipreader.domain.book.dto.response.BooksResponseDto;
import com.example.hipreader.domain.book.entity.Books;
import com.example.hipreader.domain.book.repository.BooksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class BooksService {

    private final BooksRepository booksRepository;

    @Transactional
    public BooksResponseDto registerBook(BooksRequestDto dto) {
        Books book = Books.builder()
                .genre(dto.getGenre())
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .author(dto.getAuthor())
                .publisher(dto.getPublisher())
                .datePublished(dto.getDatePublished())
                .totalPages(dto.getTotalPages())
                .coverImage(dto.getCoverImage())
                .build();
        Books register = booksRepository.save(book);
        return new BooksResponseDto(register);
    }

    @Transactional
    public BooksResponseDto updateBook(Long id, BooksRequestDto dto) {
        Books book = booksRepository.findById(id).orElseThrow(
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
        Books book = booksRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Not found book with id: " + id)
        );

        return new BooksResponseDto(book);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<BooksResponseDto> findAllBooks(Pageable pageable) {

        Page<Books> books = booksRepository.findAll(pageable);
        Page<BooksResponseDto> booksResponseDtos = books.map(BooksResponseDto::new);

        return new PageResponseDto<>(booksResponseDtos);
    }

    @Transactional
    public void deleteBook(@PathVariable Long id) {

        booksRepository.deleteById(id);
    }
}
