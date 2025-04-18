package com.example.hipreader.domain.user.dto.request;

import com.example.hipreader.domain.user.gender.Gender;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateUserRequestDto {
	private String nickname;
	private Integer age;
	private Gender gender;
}
