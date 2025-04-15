package com.example.hipreader.common.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Bean
	public MessageConverter jsonMessageConverter() {
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		converter.setClassMapper(classMapper());
		return converter;
	}

	@Bean
	public DefaultClassMapper classMapper() {
		DefaultClassMapper classMapper = new DefaultClassMapper();
		Map<String, Class<?>> idClassMapping = new HashMap<>();
		idClassMapping.put("notificationMessage",
			com.example.hipreader.domain.userdiscussion.dto.response.NotificationMessage.class);
		classMapper.setIdClassMapping(idClassMapping);
		return classMapper;
	}

	@Bean
	public RabbitTemplate rabbitTemplate(
		ConnectionFactory connectionFactory,
		MessageConverter messageConverter
	) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter);
		return rabbitTemplate;
	}

	@Bean
	Queue myMessageQueue() {
		return new Queue("my-message-queue", true);
	}

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
