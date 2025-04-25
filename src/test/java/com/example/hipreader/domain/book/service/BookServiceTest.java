package com.example.hipreader.domain.book.service;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.dto.response.PageResponseDto;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.dto.request.BookRequestDto;
import com.example.hipreader.domain.book.dto.response.BookResponseDto;
import com.example.hipreader.domain.book.dto.response.FindBookResponseDto;
import com.example.hipreader.domain.book.dto.response.PatchBookResponseDto;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @InjectMocks
  private BookService bookService;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private UserRepository userRepository;

  private final Long mockUserId = 1L;
  private final String mockIsbn13 = "1234567890123";

  @Test
  void success_to_registerBook() {
    // given
    BookRequestDto dto = new BookRequestDto(
        "test", "author", mockIsbn13, "publisherA",
        LocalDate.of(2025, 01, 01), 100,
        "https://example.com/image.jpg", "소설"
    );

    User user = new User(mockUserId, "john", "john@example.com", "abcd1234", 20, UserRole.ROLE_USER, Gender.MALE);
    AuthUser authUser = new AuthUser(mockUserId, user.getEmail(), user.getRole());

    when(userRepository.findUserById(mockUserId)).thenReturn(Optional.of(user));
    when(bookRepository.existsByIsbn13(mockIsbn13)).thenReturn(false);
    when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

    // when
    BookResponseDto bookResponseDto = bookService.registerBook(authUser, dto);

    // then
    assertThat(bookResponseDto.getTitle()).isEqualTo("test");
    assertThat(bookResponseDto.getAuthor()).isEqualTo("author");
    assertThat(bookResponseDto.getIsbn13()).isEqualTo(mockIsbn13);
  }

  @Test
  void fail_to_registerBook_becauseof_duplicate() {
    // given
    BookRequestDto dto = new BookRequestDto(
        "test", "author", mockIsbn13, "publisherA",
        LocalDate.of(2025, 01, 01), 100,
        "https://example.com/image.jpg", "소설"
    );
    User user = new User(mockUserId, "john", "john@example.com", "abcd1234", 20, UserRole.ROLE_USER, Gender.MALE);
    AuthUser authUser = new AuthUser(mockUserId, user.getEmail(), user.getRole());

    when(userRepository.findUserById(mockUserId)).thenReturn(Optional.of(user));
    when(bookRepository.existsByIsbn13(mockIsbn13)).thenReturn(true);

    // when&then
    assertThrows(BadRequestException.class,
        () -> bookService.registerBook(authUser, dto));

  }

  @Test
  void fail_to_registerBook_becuaseof_user_not_found() {
    BookRequestDto dto = new BookRequestDto(
        "test", "author", mockIsbn13, "publisherA",
        LocalDate.of(2025, 01, 01), 100,
        "https://example.com/image.jpg", "소설"
    );

    AuthUser authUser = new AuthUser(mockUserId, "john@example.com", UserRole.ROLE_USER);

    when(userRepository.findUserById(mockUserId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(NotFoundException.class,
        () -> bookService.registerBook(authUser, dto));
  }

  @Test
  void success_to_patchBook() {
    // given
    BookRequestDto dto = new BookRequestDto(
        "new title", "updated author", mockIsbn13, "new publisher",
        LocalDate.of(2025, 04, 01), 100,
        "https://example.com/image.jpg", "소설"
    );

    User user = new User(mockUserId, "john", "john@example.com", "abcd1234", 20, UserRole.ROLE_USER, Gender.MALE);
    AuthUser authUser = new AuthUser(mockUserId, user.getEmail(), user.getRole());

    Book book = Book.builder().id(1L).title("old title").isbn13(mockIsbn13).publisher("old publisher").datePublished(LocalDate.of(2025, 1, 1)).build();

    when(userRepository.findUserById(mockUserId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    // when
    PatchBookResponseDto responseDto = bookService.patchBook(authUser, 1L, dto);

    // then
    assertThat(responseDto.getTitle()).isEqualTo("new title");
    assertThat(responseDto.getAuthor()).isEqualTo("updated author");
  }

  @Test
  void findBook() {
    // given
    Book book = Book.builder().id(1L).title("title").isbn13(mockIsbn13).publisher("pub").datePublished(LocalDate.of(2025, 1, 1)).build();
    when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

    // when
    FindBookResponseDto findBookResponseDto = bookService.findBook(1L);

    // then
    assertThat(findBookResponseDto.getTitle()).isEqualTo("title");
  }

  @Test
  void findAllBooks() {
    // given
    Book book1 = Book.builder().id(1L).title("book1").isbn13("1234567890123").publisher("pub1").datePublished(LocalDate.now()).build();
    Book book2 = Book.builder().id(2L).title("book2").isbn13("1234567890456").publisher("pub2").datePublished(LocalDate.now()).build();

    Page<Book> books = new PageImpl<>(List.of(book1, book2));
    when(bookRepository.findAll(any(Pageable.class))).thenReturn(books);

    // when
    PageResponseDto<FindBookResponseDto> result = bookService.findAllBooks(PageRequest.of(0, 10));

    // then
    assertThat(result.getContent().size()).isEqualTo(2);
    assertThat(result.getContent().get(0).getTitle()).isEqualTo("book1");
  }

  @Test
  void deleteBook() {
    // given
    Long bookId = 1L;

    // when
    bookService.deleteBook(bookId);

    // then
    verify(bookRepository).deleteById(bookId);
  }
}