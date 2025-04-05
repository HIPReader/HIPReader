package com.example.hipreader.domain.settlement.service;

import static com.example.hipreader.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.hipreader.domain.settlement.dto.response.AnnualReportResponseDto;
import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.repository.UserRepository;
import com.example.hipreader.domain.userbook.entity.UserBook;
import com.example.hipreader.domain.userbook.repository.UserBookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementService {
	private final UserRepository userRepository;
	private final UserBookRepository userBookRepository;

	public AnnualReportResponseDto generateAnnualReport(Long userId, int year) {
		//1. 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResponseStatusException(USER_NOT_FOUND.getStatus(), USER_NOT_FOUND.getMessage()));

	// 	//2. 해당 사용자의 연간 독서 기록 조회
	// 	List<UserBook> userBooks = userBookRepository.findByUser(user,year);
	//
	// 	//3. 통계 생성
	// 	//총 읽은 책권수
	// 	int totalBooksRead = userBooks.size();
	// 	Map<String, Long> genreCount = userBooks.stream()
	// 		.map(userBook -> userBook.getBook().getGenre())
	// 		.collect(Collectors.groupingBy(genre -> genre, Collectors.counting()));
	//
	// 	String mostReadGenre = genreCount.entrySet().stream()
	// 		.max(Map.Entry.comparingByValue())
	// 		.map(Map.Entry::getKey)
	// 		.orElse("Unknown");
	// 	// 4. 결과 DTO 생성
	// 	AnnualReportResponseDto report = new AnnualReportResponseDto(
	// 		user.getNickname(),
	// 		year,
	// 		totalBooksRead,
	// 		totalPagesRead,
	// 		mostReadGenre
	// 	);
	// }
	//
	// private String calculateMostReadGenre(List<ReadingRecord> records) {
	// 	// 장르별 카운팅 로직
		return null;
	}

}
