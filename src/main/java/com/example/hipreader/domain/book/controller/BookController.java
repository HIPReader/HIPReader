package com.example.hipreader.domain.book.controller;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.example.hipreader.domain.book.dto.request.BookRequestDto;
import com.example.hipreader.domain.book.dto.response.BookResponseDto;
import com.example.hipreader.domain.book.dto.response.FindBookResponseDto;
import com.example.hipreader.domain.book.dto.response.PatchBookResponseDto;
import com.example.hipreader.domain.book.service.AladinService;
import com.example.hipreader.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {

    private final AladinService aladinService;
    private final BookService bookService;

    @PostMapping("/v1/books")
    public ResponseEntity<BookResponseDto> registerBook(
        @AuthenticationPrincipal AuthUser authUser,
        @RequestBody BookRequestDto bookRequestDto
    ) {
        BookResponseDto bookResponseDto = bookService.registerBook(authUser, bookRequestDto);

        return new ResponseEntity<>(bookResponseDto, HttpStatus.OK);
    }

    @PatchMapping("/v1/books/{id}")
    public ResponseEntity<PatchBookResponseDto> patchBook(
        @AuthenticationPrincipal AuthUser authUser,
        @PathVariable Long id,
        @RequestBody BookRequestDto bookRequestDto
    ) {
        PatchBookResponseDto patchBookResponseDto = bookService.patchBook(authUser, id, bookRequestDto);

        return new ResponseEntity<>(patchBookResponseDto, HttpStatus.OK);
    }

    @GetMapping("/v1/books")
    public ResponseEntity<PageResponseDto<FindBookResponseDto>> getBooks(
            @PageableDefault(size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponseDto<FindBookResponseDto> findBooksResponseDtoList = bookService.findAllBooks(pageable);

        return new ResponseEntity<>(findBooksResponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/v1/books/{id}")
    public ResponseEntity<FindBookResponseDto> getBookById(@PathVariable Long id) {
        FindBookResponseDto findBookResponseDto = bookService.findBook(id);

        return new ResponseEntity<>(findBookResponseDto, HttpStatus.OK);
    }

    @DeleteMapping("/v1/books/{id}")
    public ResponseEntity<BookResponseDto> deleteBookById(@PathVariable Long id) {
        bookService.deleteBook(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/v1/books/admin/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<BookResponseDto>> importBooksFromAladin(@RequestParam String keyword) {
      List<AladinBookDto> aladinBooks = aladinService.searchBooks(keyword);
      List<BookResponseDto> savedBooks = bookService.saveBooksFromAladin(aladinBooks);

      return new ResponseEntity<>(savedBooks, HttpStatus.OK);
    }
}
