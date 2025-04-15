package com.example.hipreader.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	// @Bean
	// public MessageConverter jsonMessageConverter() {
	// 	Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
	// 	converter.
	// 		"com.example.hipreader.domain.userdiscussion.dto.response",
	// 		"java.util",
	// 		"java.time"
	// 	);
	// 	return converter;
	// }

	// 2. RabbitTemplate에 컨버터 적용
	// @Bean
	// public RabbitTemplate rabbitTemplate(
	// 	ConnectionFactory connectionFactory,
	// 	MessageConverter messageConverter
	// ) {
	// 	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
	// 	rabbitTemplate.setMessageConverter(messageConverter);
	// 	return rabbitTemplate;
	// }

	@Bean
	Queue myMessageQueue() {
		return new Queue("my-message-queue", true);
	}

	// 교환기와 바인딩 추가
	@Bean
	DirectExchange myMessageExchange() {
		return new DirectExchange("my-message-exchange");
	}

	@Bean
	Binding myMessageBinding() {
		return BindingBuilder.bind(myMessageQueue())
			.to(myMessageExchange())
			.with("my.message.routingKey");
	}

	@Bean
	Queue bookScoreQueue() {
		return new Queue("book.score.queue", true);
	}

	@Bean
	DirectExchange bookScoreExchange() {
		return new DirectExchange("book.score.exchange");
	}


	@Bean
	Binding bookScorebinding() {
		return BindingBuilder.bind(bookScoreQueue())
			.to(bookScoreExchange())
			.with("book.score.routingKey");
	}

	@Bean
	Queue notificationQueue() {
		return new Queue("notification.queue", true); // durable 큐
	}

	// 교환기(exchange)와 바인딩 추가 (필요한 경우)
	@Bean
	DirectExchange notificationExchange() {
		return new DirectExchange("notification.exchange");
	}

	@Bean
	Binding notificationBinding() {
		return BindingBuilder.bind(notificationQueue())
			.to(notificationExchange())
			.with("notification.routingKey");
	}

}
