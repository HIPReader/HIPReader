package com.example.hipreader.domain.user.dto.request;

import com.example.hipreader.domain.user.vo.Gender;

import lombok.Getter;

@Getter
public class UpdateUserRequestDto {
	private String nickname;
	private Integer age;
	private Gender gender;
}
