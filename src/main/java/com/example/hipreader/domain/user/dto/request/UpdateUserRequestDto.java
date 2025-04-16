package com.example.hipreader.domain.user.dto.request;

import com.example.hipreader.domain.user.gender.Gender;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateUserRequestDto {
	@NotBlank(message = "닉네임은 필수 입력값입니다.")
	private String nickname;
	@Min(value = 14, message = "14세 이상만 가입 가능합니다.")
	private Integer age;
	@NotNull(message = "성별을 선택해주세요")
	private Gender gender;
}
