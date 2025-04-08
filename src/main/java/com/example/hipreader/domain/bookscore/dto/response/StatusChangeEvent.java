package com.example.hipreader.domain.bookscore.dto.response;

import java.io.Serializable;

import com.example.hipreader.domain.userbook.status.Status;

public record StatusChangeEvent(
	Long bookId,
	Status oldStatus,
	Status newStatus
) implements Serializable {}
