package com.example.hipreader.domain.book.controller;

import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.domain.book.dto.request.AladinBookDto;
import com.example.hipreader.domain.book.dto.request.BooksRequestDto;
import com.example.hipreader.domain.book.dto.response.BooksResponseDto;
import com.example.hipreader.domain.book.service.AladinService;
import com.example.hipreader.domain.book.service.BooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BooksController {

    private final AladinService aladinService;
    private final BooksService booksService;

    @PostMapping()
    public ResponseEntity<BooksResponseDto> registerBooks(@RequestBody BooksRequestDto booksRequestDto) {
        BooksResponseDto booksResponseDto = booksService.registerBook(booksRequestDto);

        return ResponseEntity.ok(booksResponseDto);
    }

	@PostMapping("/admin/import")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<BooksResponseDto>> importBooksFromAladin(@RequestParam String keyword) {
		List<AladinBookDto> aladinBooks = aladinService.searchBooks(keyword);
		List<BooksResponseDto> savedBooks = booksService.saveBooksFromAladin(aladinBooks);
		return ResponseEntity.ok(savedBooks);
	}

    @PatchMapping("/{id}")
    public ResponseEntity<BooksResponseDto> updateBooks(@PathVariable Long id,
                                                        @RequestBody BooksRequestDto booksRequestDto) {
        BooksResponseDto booksResponseDto = booksService.updateBook(id, booksRequestDto);

        return ResponseEntity.ok(booksResponseDto);
    }

    @GetMapping()
    public ResponseEntity<PageResponseDto<BooksResponseDto>> getBooks(
            @PageableDefault(size = 10,
                    sort = "updatedAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        PageResponseDto<BooksResponseDto> booksResponseDtoList = booksService.findAllBooks(pageable);

        return ResponseEntity.ok(booksResponseDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BooksResponseDto> getBookById(@PathVariable Long id) {
        BooksResponseDto booksResponseDto = booksService.findBook(id);

        return ResponseEntity.ok(booksResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BooksResponseDto> deleteBookById(@PathVariable Long id) {
        booksService.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}
