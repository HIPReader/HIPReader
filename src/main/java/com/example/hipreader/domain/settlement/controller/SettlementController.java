package com.example.hipreader.domain.settlement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.domain.settlement.producer.SettlementProducer;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SettlementController {
	private final SettlementProducer settlementProducer;

	@PostMapping("/settlement/{userId}")
	public ResponseEntity<String> triggerAnnualSettlement(
		@PathVariable Long userId,
		@RequestParam int year
	) {
		settlementProducer.requestAnnualSettlement(userId, year);
		return ResponseEntity.ok("결산 요청이 큐에 추가되었습니다.");
	}
}
