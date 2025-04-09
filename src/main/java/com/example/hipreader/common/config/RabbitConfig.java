package com.example.hipreader.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Bean
	Queue bookScoreQueue() {
		return new Queue("book.score.queue", true);
	}

	@Bean
	DirectExchange bookScoreExchange() {
		return new DirectExchange("book.score.exchange");
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue)
			.to(exchange)
			.with("book.score.routingKey");
	}
}
