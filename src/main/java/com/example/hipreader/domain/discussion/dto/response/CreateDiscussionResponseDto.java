package com.example.hipreader.domain.discussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.discussion.status.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDiscussionResponseDto {

	private Long id;
	private String topic;
	private LocalDateTime scheduledAt;
	private Integer participants;
	private Status status;
	private String message;

	private Long hostId;
	private String hostName;
}
