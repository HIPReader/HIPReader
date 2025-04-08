package com.example.hipreader.domain.user.dto.response;

import com.example.hipreader.domain.user.gender.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserResponseDto {
	private Long userId;
	private String email;
	private String nickname;
	private Integer age;
	private Gender gender;
}
