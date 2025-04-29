package com.example.hipreader.domain.discussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.discussion.entity.Discussion;
import com.example.hipreader.domain.discussion.status.Status;
import com.example.hipreader.domain.userdiscussion.status.DiscussionMode;

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
	private DiscussionMode mode;
	private String message;
	private Long hostId;
	private String hostName;

	public static CreateDiscussionResponseDto toDto(Discussion discussion) {
		return CreateDiscussionResponseDto.builder()
			.id(discussion.getId())
			.topic(discussion.getTopic())
			.scheduledAt(discussion.getScheduledAt())
			.participants(discussion.getParticipants())
			.status(discussion.getStatus())
			.mode(discussion.getMode())
			.message("성공적으로 토론방이 생성되었습니다.")
			.hostId(discussion.getUser().getId())
			.hostName(discussion.getUser().getNickname())
			.build();
	}
}
