package com.example.hipreader.domain.discussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.status.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetDiscussionResponseDto {

	private final Long id;
	private final String topic;
	private final Integer participants;
	private final LocalDateTime scheduledAt;
	private final Status status;
	private final Long userId;
	private final Long bookId;

	public static GetDiscussionResponseDto toDto(Discussion discussion) {
		return GetDiscussionResponseDto.builder()
			.id(discussion.getId())
			.topic(discussion.getTopic())
			.participants(discussion.getParticipants())
			.scheduledAt(discussion.getScheduledAt())
			.status(discussion.getStatus())
			.userId(discussion.getUser().getId())
			.bookId(discussion.getBook().getId())
			.build();
	}
}
