package com.example.hipreader.domain.settlement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettlementRequestDto {
	private Long userId;
	private int year;
}
