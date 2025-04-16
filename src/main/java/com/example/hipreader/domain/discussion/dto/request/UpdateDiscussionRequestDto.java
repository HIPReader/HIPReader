package com.example.hipreader.domain.discussion.dto.request;

import java.time.LocalDateTime;

import com.example.hipreader.domain.discussion.status.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateDiscussionRequestDto {

	private String topic;
	private Integer participants;
	private LocalDateTime scheduledAt;
	private Status status;
}
