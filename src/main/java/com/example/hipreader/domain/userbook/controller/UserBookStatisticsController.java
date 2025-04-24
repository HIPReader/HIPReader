package com.example.hipreader.domain.userbook.controller;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userbook.dto.response.AverageStaticResponseDto;
import com.example.hipreader.domain.userbook.dto.response.YearlyStaticResponseDto;
import com.example.hipreader.domain.userbook.service.UserBookStaticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserBookStatisticsController {

  private final UserBookStaticsService userBookStaticsService;

  @GetMapping("/v1/statics/yearly")
  public ResponseEntity<YearlyStaticResponseDto> getYearlyStatistics(
      @AuthenticationPrincipal AuthUser authUser,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
      ) {
    return new ResponseEntity<>(userBookStaticsService.getYearlyStatics(authUser, page, size), HttpStatus.OK);
  }

  @GetMapping("/v1/statics/average")
  public ResponseEntity<AverageStaticResponseDto> getAverageStatics(@AuthenticationPrincipal AuthUser authUser) {
    return new ResponseEntity<>(userBookStaticsService.getAverageStatics(authUser), HttpStatus.OK);
  }
}
