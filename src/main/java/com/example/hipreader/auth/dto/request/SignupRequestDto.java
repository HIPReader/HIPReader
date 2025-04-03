package com.example.hipreader.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
	//닉네임, 이메일, 비밀번호, 권한 ,성별 ,나이

	private String email;

	private String password;

	private String nickname;

	private Integer age;

	private String gender;

	private String role;
}
