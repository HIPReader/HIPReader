package com.example.hipreader.domain.bookscore.listener;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.domain.bookscore.dto.response.StatusChangeEvent;
import com.example.hipreader.domain.bookscore.service.BookScoreFacadeService;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookScoreEventListener {

	private final BookScoreFacadeService bookScoreFacadeService;
	private final RabbitTemplate rabbitTemplate;

	@RabbitListener(queues = "book.score.queue")
	@Transactional
	public void handleStatusChange(StatusChangeEvent event, Message message, Channel channel) {
		try {
			bookScoreFacadeService.handleStatusChange(
				event.bookId(),
				event.oldStatus(),
				event.newStatus()
			);
		} catch (Exception ex) {
			// 3회 재시도 후 DLQ 전송
			if (checkRetryCount(message)) {
				sendToDlq(message, "x.dlx.book.score", "book.score.dlq.routingKey");
			} else {
				requeueMessage(channel, message);
			}
		}
	}

	private boolean checkRetryCount(Message message) {
		Integer retryCount = message.getMessageProperties().getHeader("retry-count");
		if (retryCount == null) retryCount = 0;
		return retryCount >= 3;
	}

	private void sendToDlq(Message message, String exchange, String routingKey) {
		rabbitTemplate.send(exchange, routingKey, message);
	}

	private void requeueMessage(Channel channel, Message message) {
		try {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
