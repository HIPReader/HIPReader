package com.example.hipreader.domain.userbook.service;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.book.entity.Book;
import com.example.hipreader.domain.book.repository.BookRepository;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userbook.dto.request.UserBookRequestDto;
import com.example.hipreader.domain.userbook.dto.response.UserBookResponseDto;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    public UserBookResponseDto registerUserBook(AuthUser authUser, UserBookRequestDto userBookRequestDto) {
        User user = userRepository.findUserById(authUser.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(userBookRequestDto.getBookId()).orElseThrow(() -> new RuntimeException("Book not found"));

        if(userBookRepository.existsByUserandBook(user, book)) {
            throw new RuntimeException("이 책은 이미 등록되어 있습니다.");
        }

        UserBook userBook = UserBook.builder()
                .user(user)
                .book(book)
                .progress(0L)
                .status(userBookRequestDto.getStatus())
                .build();

        userBookRepository.save(userBook);

        return new UserBookResponseDto(user, book, userBook.getProgress(), userBook.getStatus());
    }
}
