package com.example.hipreader.domain.settlement.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SettlementRabbitMQConfig {
	public static final String SETTLEMENT_QUEUE = "settlement.queue";
	public static final String SETTLEMENT_EXCHANGE = "settlement.exchange";
	public static final String ROUTING_KEY = "settlement.key";

	@Bean
	public Queue settlementQueue() {
		return new Queue(SETTLEMENT_QUEUE, true);
	}

	@Bean
	public DirectExchange settlementExchange() {
		return new DirectExchange(SETTLEMENT_EXCHANGE);
	}

	@Bean
	public Binding settlementBinding() {
		return BindingBuilder.bind(settlementQueue())
			.to(settlementExchange())
			.with(ROUTING_KEY);
	}
}
