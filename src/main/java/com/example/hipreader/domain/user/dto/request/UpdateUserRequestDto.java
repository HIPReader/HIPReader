package com.example.hipreader.domain.user.dto.request;

import com.example.hipreader.domain.user.vo.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequestDto {
	private String nickname;
	private Integer age;
	private Gender gender;
}
