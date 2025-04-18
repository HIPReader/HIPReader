package com.example.hipreader.domain.userbook.service;

import com.example.hipreader.auth.dto.AuthUser;
import static com.example.hipreader.common.exception.ErrorCode.USER_NOT_FOUND;
import com.example.hipreader.common.exception.NotFoundException;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userbook.dto.response.AverageStaticResponseDto;
import com.example.hipreader.domain.userbook.dto.response.YearlyStaticResponseDto;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;
import com.example.hipreader.domain.userbook.status.Status;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBookStaticsService {

  private final UserBookRepository userBookRepository;
  private final UserRepository userRepository;

  public YearlyStaticResponseDto getYearlyStatics(AuthUser authUser, int page, int size) {
    Pageable pageable = PageRequest.of(page-1, size);

    int year = LocalDateTime.now().getYear();

    LocalDateTime start = LocalDate.of(year, 1,1).atStartOfDay();
    LocalDateTime end = LocalDate.of(year, 12, 31).atStartOfDay();

    User user = userRepository.findUserById(authUser.getId())
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

    Page<UserBook> finishedBooks = userBookRepository.findByUserAndStatusAndCreatedAtBetween(user, Status.FINISHED, start, end, pageable);

    return YearlyStaticResponseDto.toDto(user, finishedBooks, pageable);
  }

  public AverageStaticResponseDto getAverageStatics(AuthUser authUser) {
    User user = userRepository.findUserById(authUser.getId())
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

    List<UserBook> finishedBooks = userBookRepository.findByUserAndStatus(user, Status.FINISHED);

    long totalBooks = finishedBooks.size();

    if(totalBooks == 0) {
      return new AverageStaticResponseDto(0,0,0);
    }

    double averageDays = finishedBooks.stream()
        .mapToLong(book -> Duration.between(book.getCreatedAt(), book.getUpdatedAt()).toDays())
        .average()
        .orElse(0);

    double averagePages = finishedBooks.stream()
        .mapToInt(book -> book.getBook().getTotalPages())
        .average().orElse(0);

    double avgDays = Math.round(averageDays * 10.0) / 10.0;
    double avgPages = Math.round(averagePages * 10.0) / 10.0;

    return new AverageStaticResponseDto(avgDays, avgPages, totalBooks);
  }
}
