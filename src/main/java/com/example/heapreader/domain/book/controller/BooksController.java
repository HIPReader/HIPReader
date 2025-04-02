package com.example.heapreader.domain.book.controller;

import com.example.heapreader.domain.book.dto.request.BooksRequestDto;
import com.example.heapreader.domain.book.dto.response.BooksResponseDto;
import com.example.heapreader.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BooksController {

    private final BookService booksService;

    @PostMapping()
    public ResponseEntity<BooksResponseDto> registerBooks(@RequestBody BooksRequestDto booksRequestDto) {
        BooksResponseDto booksResponseDto = booksService.registerBook(booksRequestDto);

        return ResponseEntity.ok(booksResponseDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BooksResponseDto> updateBooks(@PathVariable Long id,
                                                        @RequestBody BooksRequestDto booksRequestDto) {
        BooksResponseDto dto = booksService.updateBook(id, booksRequestDto);

        return ResponseEntity.ok(dto);
    }

    @GetMapping()
    public ResponseEntity<List<BooksResponseDto>> getBooks() {
        List<BooksResponseDto> booksResponseDtoList = booksService.findAllBooks();
        return ResponseEntity.ok(booksResponseDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BooksResponseDto> getBookById(@PathVariable Long id) {
        BooksResponseDto dto = booksService.findBook(id);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BooksResponseDto> deleteBookById(@PathVariable Long id) {
        booksService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}
