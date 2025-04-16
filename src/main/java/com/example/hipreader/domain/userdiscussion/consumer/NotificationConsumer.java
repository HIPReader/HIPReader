package com.example.hipreader.domain.userdiscussion.consumer;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.hipreader.domain.userdiscussion.dto.response.NotificationMessage;
import com.example.hipreader.domain.userdiscussion.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

	private final NotificationService notificationService;

	@RabbitListener(queues = "notification.queue")
	public void handleMessage(NotificationMessage message) {
		try {
			switch (message.eventType()) {
				case "PENDING" ->
					notificationService.sendApplyAlert(message);
				case "APPROVED" ->
					notificationService.sendApprovalAlert(message);
				case "REJECTED" ->
					notificationService.sendRejectionAlert(message);
			}
		} catch (Exception e) {
			log.error("알림 처리 실패: {}", e.getMessage());
			throw new AmqpRejectAndDontRequeueException(e);
		}
	}
}
