package com.example.hipreader.domain.userdiscussion.dto.response;

import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.vo.Gender;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetUserDiscussionResponseDto {
	private final Long userDiscussionId;
	private final Long userId;
	private final Long discussionId;

	private final String email;
	private final String nickname;
	private final Integer age;
	private final Gender gender;

	public static GetUserDiscussionResponseDto toDto(UserDiscussion userDiscussion) {
		User user = userDiscussion.getUser();
		return GetUserDiscussionResponseDto.builder()
			.userDiscussionId(userDiscussion.getId())
			.userId(user.getId())
			.discussionId(userDiscussion.getDiscussion().getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.age(user.getAge())
			.gender(user.getGender())
			.build();
	}
}
