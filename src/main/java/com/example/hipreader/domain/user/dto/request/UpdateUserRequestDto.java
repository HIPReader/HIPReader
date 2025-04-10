package com.example.hipreader.domain.user.dto.request;

import com.example.hipreader.domain.user.gender.Gender;

import lombok.Getter;

@Getter
public class UpdateUserRequestDto {
	private String nickname;
	private Integer age;
	private Gender gender;

}
