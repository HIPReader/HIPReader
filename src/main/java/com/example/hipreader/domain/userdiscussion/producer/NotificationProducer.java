package com.example.hipreader.domain.userdiscussion.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.hipreader.domain.userdiscussion.dto.response.NotificationMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationProducer {

	private final RabbitTemplate rabbitTemplate;

	public void sendNotification(NotificationMessage message) {
		rabbitTemplate.convertAndSend(
			"notification.exchange",
			"notification.routingKey",
			message
		);
	}
}
