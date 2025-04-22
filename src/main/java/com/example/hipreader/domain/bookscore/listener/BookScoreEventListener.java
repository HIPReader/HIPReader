package com.example.hipreader.domain.bookscore.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.hipreader.domain.bookscore.dto.response.StatusChangeEvent;
import com.example.hipreader.domain.bookscore.service.BookScoreService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookScoreEventListener {

	private final BookScoreService bookScoreService;

	@RabbitListener(queues = "book.score.queue")
	@Transactional
	public void handleStatusChange(StatusChangeEvent event) {
		bookScoreService.handleStatusChange(
			event.bookId(),
			event.oldStatus(),
			event.newStatus()
		);
	}
}
