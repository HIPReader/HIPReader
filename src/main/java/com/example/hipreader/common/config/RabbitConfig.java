package com.example.hipreader.common.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hipreader.domain.bookscore.dto.response.StatusChangeEvent;
import com.example.hipreader.domain.userdiscussion.dto.response.NotificationMessage;

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
		idClassMapping.put("notificationMessage", NotificationMessage.class);
		idClassMapping.put("statusChangeEvent", StatusChangeEvent.class);
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
		return QueueBuilder.durable("book.score.queue")
			.withArgument("x-dead-letter-exchange", "x.dlx.book.score") // DLX 지정
			.withArgument("x-dead-letter-routing-key", "book.score.dlq.routingKey") // DLQ 라우팅 키
			.build();
	}

	@Bean
	DirectExchange userbookExchange() {
		return new DirectExchange("userbook.exchange");
	}

	// 올바른 바인딩 설정 추가
	@Bean
	Binding statusChangeBinding() {
		return BindingBuilder.bind(bookScoreQueue())
			.to(userbookExchange())  // 실제 사용하는 Exchange
			.with("userbook.status.change"); // 실제 사용하는 라우팅 키
	}

	@Bean
	Queue notificationQueue() {
		return QueueBuilder.durable("notification.queue")
			.withArgument("x-dead-letter-exchange", "x.dlx.notification")
			.withArgument("x-dead-letter-routing-key", "notification.dlq.routingKey")
			.build();
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

	// Book Score 관련 DLQ 설정
	@Bean
	public DirectExchange dlxBookScore() {
		return new DirectExchange("x.dlx.book.score");
	}

	@Bean
	public Queue dlqBookScore() {
		return new Queue("q.dlq.book.score", true); // durable 큐
	}

	@Bean
	public Binding dlqBindingBookScore() {
		return BindingBuilder.bind(dlqBookScore())
			.to(dlxBookScore())
			.with("book.score.dlq.routingKey");
	}

	// Notification 관련 DLQ 설정
	@Bean
	public DirectExchange dlxNotification() {
		return new DirectExchange("x.dlx.notification");
	}

	@Bean
	public Queue dlqNotification() {
		return new Queue("q.dlq.notification", true);
	}

	@Bean
	public Binding dlqBindingNotification() {
		return BindingBuilder.bind(dlqNotification())
			.to(dlxNotification())
			.with("notification.dlq.routingKey");
	}

}
