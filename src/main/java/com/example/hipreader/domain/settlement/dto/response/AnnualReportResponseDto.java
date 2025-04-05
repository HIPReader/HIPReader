package com.example.hipreader.domain.settlement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnnualReportResponseDto {
	private String nickname;
	private int year;
	private int totalBooksRead;
	private String mostReadGenre;
}
