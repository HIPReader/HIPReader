package com.example.hipreader.domain.user.dto.response;

import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.vo.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetUserResponseDto {

	private final Long userId;
	private final String nickname;
	private final String email;
	private final Integer age;
	private final Gender gender;

	public static GetUserResponseDto toDto(User user) {
		return GetUserResponseDto.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.age(user.getAge())
			.gender(user.getGender())
			.nickname(user.getNickname())
			.build();
	}
}
