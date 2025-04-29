package com.example.hipreader.domain.userdiscussion.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.userdiscussion.applicationStatus.ApplicationStatus;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RejectUserDiscussionResponseDto {
	private final ApplicationStatus status;
	private final LocalDateTime rejectedAt;

	public static RejectUserDiscussionResponseDto toDto(UserDiscussion userDiscussion) {
		return RejectUserDiscussionResponseDto.builder()
			.status(userDiscussion.getStatus())
			.rejectedAt(userDiscussion.getStatusUpdatedAt())
			.build();
	}
}
