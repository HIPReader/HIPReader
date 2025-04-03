package com.example.hipreader.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
	//닉네임, 이메일, 비밀번호, 권한 ,성별 ,나이
	@NotBlank
	@Email
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	private String nickname;
	@NotBlank
	private Integer age;
	@NotBlank
	private String gender;
	@NotBlank
	private String userRole;
}
