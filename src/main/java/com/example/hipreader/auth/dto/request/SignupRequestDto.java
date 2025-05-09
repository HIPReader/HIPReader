package com.example.hipreader.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
	//닉네임, 이메일, 비밀번호, 권한 ,성별 ,나이
	@NotBlank
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	private String nickname;
	@NotNull
	private Integer age;
	@NotNull
	private String gender;
	@NotNull
	private String role;
}
