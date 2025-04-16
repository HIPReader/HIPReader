package com.example.hipreader.domain.userdiscussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.userdiscussion.ApplicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ApplyUserDiscussionResponseDto {
	private final Long id;
	private final Long userId;
	private final Long discussionId;
	private final LocalDateTime appliedAt;
	private final ApplicationStatus status;

	public static ApplyUserDiscussionResponseDto toDto(UserDiscussion userDiscussion) {

		return ApplyUserDiscussionResponseDto.builder()
			.id(userDiscussion.getId())
			.userId(userDiscussion.getUser().getId())
			.discussionId(userDiscussion.getDiscussion().getId())
			.appliedAt(userDiscussion.getAppliedAt())
			.status(userDiscussion.getStatus())
			.build();
	}
}
