package com.example.hipreader.domain.userdiscussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApproveUserDiscussionResponseDto {

	private final ApplicationStatus status;
	private final LocalDateTime approvedAt;

	public static ApproveUserDiscussionResponseDto toDto(UserDiscussion userDiscussion) {
		return ApproveUserDiscussionResponseDto.builder()
			.status(userDiscussion.getStatus())
			.approvedAt(userDiscussion.getStatusUpdatedAt())
			.build();
	}
}
