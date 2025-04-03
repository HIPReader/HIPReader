package com.example.hipreader.domain.userbook.service;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userbook.dto.request.RegisterUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.request.UpdateUserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.response.UserBookResponseDto;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;
import com.example.hipreader.domain.userbook.status.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserBookResponseDto registerUserBook(AuthUser authUser, RegisterUserBookRequestDto registerUserBookRequestDto) {
        User user = userRepository.findUserById(authUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(registerUserBookRequestDto.getBookId()).orElseThrow(() -> new RuntimeException("Book not found"));

        if(userBookRepository.existsByUserAndBook(user, book)) {
            throw new RuntimeException("이 책은 이미 등록되어 있습니다.");
        }

        UserBook userBook = UserBook.builder()
                .user(user)
                .book(book)
                .progress(0)
                .status(registerUserBookRequestDto.getStatus())
                .build();

        userBookRepository.save(userBook);

        return new UserBookResponseDto(user, book, userBook.getProgress(), userBook.getStatus());
    }

    @Transactional
    public Page<UserBookResponseDto> findReadingBooks(AuthUser authUser, int page, int size) {
        User user = userRepository.findUserById(authUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page-1, size);

        Page<UserBook> userBooks = userBookRepository.findByUser(user, pageable);

        return userBooks.map(userBook ->
                new UserBookResponseDto(userBook.getUser(), userBook.getBook(), userBook.getProgress(), userBook.getStatus()));
    }

    @Transactional
    public UserBookResponseDto findReadingBook(AuthUser authUser, Long userbookId) {
        User user = userRepository.findUserById(authUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        UserBook userBook = userBookRepository.findByIdAndUser(userbookId, user);

        return new UserBookResponseDto(userBook.getUser(), userBook.getBook(), userBook.getProgress(), userBook.getStatus());
    }

    @Transactional
    public UserBookResponseDto updateUserBook(AuthUser authUser,UpdateUserBookRequestDto updateUserBookRequestDto, Long userbookId) {
        User user = userRepository.findUserById(authUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        UserBook userBook = userBookRepository.findByIdAndUser(userbookId, user);
        if (userBook == null) { throw new RuntimeException("등록된 책이 없습니다."); }

        userBook.update(updateUserBookRequestDto.getStatus(), updateUserBookRequestDto.getProgress());

        return new UserBookResponseDto(userBook.getUser(), userBook.getBook(), userBook.getProgress(), userBook.getStatus());
    }

    @Transactional
    public void deleteUserBook(AuthUser authUser, Long userBookId) {
        User user = userRepository.findUserById(authUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserBook userBook = userBookRepository.findByIdAndUser(userBookId, user);
        if (userBook == null) {
            throw new RuntimeException("해당 책은 사용자의 책이 아닙니다.");
        }

        UserBookResponseDto responseDto = new UserBookResponseDto(
                userBook.getUser(),
                userBook.getBook(),
                userBook.getProgress(),
                userBook.getStatus()
        );

        userBookRepository.delete(userBook);
    }

    @Transactional
    public Page<UserBookResponseDto> findWishBooks(AuthUser authUser, int page, int size) {
        User user = userRepository.findUserById(authUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page-1, size);
        Page<UserBook> userBooks = userBookRepository.findByUserAndStatus(user, Status.TO_READ, pageable);

        return userBooks.map(userBook ->
                new UserBookResponseDto(userBook.getUser(), userBook.getBook(), userBook.getProgress(), userBook.getStatus()));
    }
}
