package com.example.hipreader.domain.settlement.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.hipreader.domain.settlement.config.SettlementRabbitMQConfig;
import com.example.hipreader.domain.settlement.dto.request.SettlementRequestDto;
import com.example.hipreader.domain.settlement.service.SettlementService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SettlementConsumer {
	private final SettlementService settlementService;

	@RabbitListener(queues = SettlementRabbitMQConfig.SETTLEMENT_QUEUE)
	public void processSettlementRequest(SettlementRequestDto request) {
		settlementService.generateAnnualReport(
			request.getUserId(),
			request.getYear()
		);
	}
}
