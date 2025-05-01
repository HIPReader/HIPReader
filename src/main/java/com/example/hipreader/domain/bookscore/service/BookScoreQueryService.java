package com.example.hipreader.domain.bookscore.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.domain.bookscore.dto.response.GetBookOfYearResponseDto;
import com.example.hipreader.domain.bookscore.entity.YearlyTopBook;
import com.example.hipreader.domain.bookscore.repository.YearlyTopBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookScoreQueryService {
	private final YearlyTopBookRepository yearlyTopBookRepository;

	@Cacheable(value = "yearlyTopBooks",key = "#year")
	public GetBookOfYearResponseDto getBookOfTheYear(int year) {
		List<YearlyTopBook> topBooks = yearlyTopBookRepository.findByYear(year);
		return GetBookOfYearResponseDto.from(year,topBooks);
	}
}
