package com.example.hipreader.domain.userbook.service;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.common.exception.BadRequestException;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.bookscore.dto.response.StatusChangeEvent;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.user.vo.UserRole;
import com.example.hipreader.domain.userbook.dto.request.RegisterUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.request.UpdateUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.response.GetUserBookResponseDto;
import com.example.hipreader.domain.userbook.dto.response.RegisterUSerBookResponseDto;
import com.example.hipreader.domain.userbook.dto.response.UpdateUserBookResponseDto;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;
import com.example.hipreader.domain.userbook.status.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserBookServiceTest {

  @InjectMocks
  private UserBookService userBookService;

  @Mock
  private BookRepository bookRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserBookRepository userBookRepository;
  @Mock
  private RabbitTemplate rabbitTemplate;

  @Test
  void success_to_registerUserBook() {
    // given
    Long userId = 1L;
    Long bookId = 10L;
    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    RegisterUserBookRequestDto dto = new RegisterUserBookRequestDto(bookId, Status.TO_READ);
    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(userBookRepository.existsByUserAndBook(user, book)).thenReturn(false);
    when(userBookRepository.save(any(UserBook.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    RegisterUSerBookResponseDto response = userBookService.registerUserBook(authUser, dto);

    // then
    assertThat(response.getUsername()).isEqualTo("john");
    assertThat(response.getStatus()).isEqualTo(Status.TO_READ);
    assertThat(response.getTitle()).isEqualTo("title");
    assertThat(response.getPercentage()).isEqualTo(0);
  }

  @Test
  void fail_to_registerUserBook_whenUserNotFound() {
    Long userId = 1L;
    Long bookId = 10L;

//    Book book = Book.builder()
//        .id(bookId)
//        .title("title")
//        .isbn13("1234567890123")
//        .publisher("pub")
//        .coverImage("https://example.com/image.jpg")
//        .categoryName("소설")
//        .datePublished(LocalDate.of(2025, 01, 01))
//        .totalPages(100)
//        .build();
    RegisterUserBookRequestDto dto = new RegisterUserBookRequestDto(bookId, Status.TO_READ);
    AuthUser authUser = new AuthUser(userId, "john@example.com", UserRole.ROLE_USER);

    when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(NotFoundException.class,
        () -> userBookService.registerUserBook(authUser, dto));

  }

  @Test
  void fail_to_registerBook_whenBookNotFound() {
    Long userId = 1L;
    Long bookId = 10L;

    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);

    RegisterUserBookRequestDto dto = new RegisterUserBookRequestDto(bookId, Status.TO_READ);
    AuthUser authUser = new AuthUser(userId, "john@example.com", UserRole.ROLE_USER);

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(NotFoundException.class,
        () -> userBookService.registerUserBook(authUser, dto));

  }

  @Test
  void fail_to_registerBook_whenBookAlreadyExists() {
    Long userId = 1L;
    Long bookId = 10L;
    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    RegisterUserBookRequestDto dto = new RegisterUserBookRequestDto(bookId, Status.TO_READ);
    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(userBookRepository.existsByUserAndBook(user, book)).thenReturn(true);

    // when & then
    assertThrows(BadRequestException.class,
        () -> userBookService.registerUserBook(authUser, dto));
  }

  @Test
  void findReadingBooks() {
    // given
    Long userId = 1L;
    Long bookId = 10L;
    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    UserBook userBook = UserBook.builder().user(user).book(book).status(Status.READING).progress(50).build();

    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    Page<UserBook> page = new PageImpl<>(List.of(userBook));
    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(userBookRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);

    // when
    Page<GetUserBookResponseDto> result = userBookService.findAllUserBooks(authUser, 1, 10);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getTitle()).isEqualTo("title");
  }

  @Test
  void findReadingBook() {
    // given
    Long userId = 1L;
    Long bookId = 10L;

    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    UserBook userBook = UserBook.builder().user(user).book(book).status(Status.READING).progress(50).build();

    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(userBookRepository.findByIdAndUser(bookId, user)).thenReturn(userBook);

    // when
    GetUserBookResponseDto response = userBookService.findUserBook(authUser, bookId);

    // then
    assertThat(response.getTitle()).isEqualTo("title");
    assertThat(response.getUsername()).isEqualTo("john");
  }

  @Test
  void success_to_updateUserBook() {
    // given
    Long userId = 1L;
    Long bookId = 10L;
    Long userBookId = 100L;

    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    UserBook userbook = UserBook.builder().id(userBookId).user(user).book(book).status(Status.TO_READ).progress(0).build();
    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    UpdateUserBookRequestDto dto = new UpdateUserBookRequestDto(Status.READING, 50);

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(userBookRepository.findByIdAndUser(userBookId, user)).thenReturn(userbook);

    // when
    UpdateUserBookResponseDto response = userBookService.updateUserBook(authUser, dto, userBookId);

    //then
    assertThat(response.getStatus()).isEqualTo(Status.READING);
    assertThat(response.getPercentage()).isEqualTo(50);

    verify(rabbitTemplate).convertAndSend(
        eq("userbook.exchange"),
        eq("userbook.status.change"),
        any(StatusChangeEvent.class)
    );
  }

  @Test
  void fail_to_updateUserBook_already_finished() {
    // given
    Long userId = 1L;
    Long bookId = 10L;
    Long userBookId = 100L;

    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    UserBook userbook = UserBook.builder().id(userBookId).user(user).book(book).status(Status.FINISHED).progress(100).build();
    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    UpdateUserBookRequestDto dto = new UpdateUserBookRequestDto(Status.READING, 90);

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(userBookRepository.findByIdAndUser(userBookId, user)).thenReturn(userbook);

    assertThrows(BadRequestException.class, () -> userBookService.updateUserBook(authUser, dto, userBookId));
  }

  @Test
  void deleteUserBook() {
    // given
    Long userId = 1L;
    Long bookId = 10L;
    Long userBookId = 100L;

    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    UserBook userbook = UserBook.builder().id(userBookId).user(user).book(book).status(Status.READING).progress(50).build();
    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(userBookRepository.findByIdAndUser(userBookId, user)).thenReturn(userbook);

    // when
    userBookService.deleteUserBook(authUser, userBookId);

    // then
    verify(userBookRepository).delete(userbook);
  }

  @Test
  void findWishBooks() {
    // given
    Long userId = 1L;
    Long bookId = 10L;
    Long userBookId = 100L;

    User user = new User(userId, "john", "john@example.com", "abcd1234!",  20, UserRole.ROLE_USER, Gender.MALE);
    Book book = Book.builder()
        .id(bookId)
        .title("title")
        .isbn13("1234567890123")
        .publisher("pub")
        .coverImage("https://example.com/image.jpg")
        .categoryName("소설")
        .datePublished(LocalDate.of(2025, 01, 01))
        .totalPages(100)
        .build();

    UserBook userbook = UserBook.builder().id(userBookId).user(user).book(book).status(Status.TO_READ).build();
    AuthUser authUser = new AuthUser(userId, user.getEmail(), user.getRole());

    Page<UserBook> page = new PageImpl<>(List.of(userbook));

    when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
    when(userBookRepository.findByUserAndStatus(eq(user), eq(Status.TO_READ),any(Pageable.class))).thenReturn(page);

    // when
    Page<GetUserBookResponseDto> result = userBookService.findWishBooks(authUser, 1, 10);

    // then
    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getTitle()).isEqualTo("title");
    assertThat(result.getContent().get(0).getStatus()).isEqualTo(Status.TO_READ);
  }
}