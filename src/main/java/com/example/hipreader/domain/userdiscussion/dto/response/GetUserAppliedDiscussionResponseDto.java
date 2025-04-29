package com.example.hipreader.domain.userdiscussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserAppliedDiscussionResponseDto {
	private final Long userDiscussionId;
	private final Long discussionId;
	private final String discussionTopic;
	private final Integer participants;
	private final String discussionStatus;
	private final String applicationStatus;
	private final LocalDateTime scheduledAt;
	private final LocalDateTime appliedAt;

	public static GetUserAppliedDiscussionResponseDto toDto(UserDiscussion userDiscussion) {
		return GetUserAppliedDiscussionResponseDto.builder()
			.userDiscussionId(userDiscussion.getId())
			.discussionId(userDiscussion.getDiscussion().getId())
			.discussionTopic(userDiscussion.getDiscussion().getTopic())
			.participants(userDiscussion.getDiscussion().getParticipants())
			.discussionStatus(userDiscussion.getDiscussion().getStatus().name())
			.applicationStatus(userDiscussion.getStatus().name())
			.scheduledAt(userDiscussion.getDiscussion().getScheduledAt())
			.appliedAt(userDiscussion.getAppliedAt())
			.build();
	}
}
