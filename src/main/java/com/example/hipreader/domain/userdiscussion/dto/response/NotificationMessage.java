package com.example.hipreader.domain.userdiscussion.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public record NotificationMessage(
	Long userId,
	Long discussionId,
	String eventType,
	LocalDateTime eventTime
) implements Serializable {
}
