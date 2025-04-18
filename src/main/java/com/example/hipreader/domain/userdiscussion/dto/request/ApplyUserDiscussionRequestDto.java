package com.example.hipreader.domain.userdiscussion.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyUserDiscussionRequestDto {
	private Long discussionId;
}
