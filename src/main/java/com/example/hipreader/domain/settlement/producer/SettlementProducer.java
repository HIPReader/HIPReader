package com.example.hipreader.domain.settlement.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.hipreader.domain.settlement.config.SettlementRabbitMQConfig;
import com.example.hipreader.domain.settlement.dto.request.SettlementRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementProducer {
	private final RabbitTemplate rabbitTemplate;

	public void requestAnnualSettlement(Long userId, int year) {
		SettlementRequestDto request = new SettlementRequestDto(userId, year);
		rabbitTemplate.convertAndSend(
			SettlementRabbitMQConfig.SETTLEMENT_EXCHANGE,
			SettlementRabbitMQConfig.ROUTING_KEY,
			request
		);
	}
}
