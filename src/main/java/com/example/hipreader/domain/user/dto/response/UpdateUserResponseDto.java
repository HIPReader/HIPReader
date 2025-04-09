package com.example.hipreader.domain.user.dto.response;

import com.example.hipreader.domain.user.entity.User;
import com.example.hipreader.domain.user.gender.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateUserResponseDto {
	private Long userId;
	private String email;
	private String nickname;
	private Integer age;
	private Gender gender;

	public static UpdateUserResponseDto toDto(User user) {
		return UpdateUserResponseDto.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.age(user.getAge())
			.gender(user.getGender())
			.build();
	}
}
