package com.example.hipreader.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordRequestDto {

	@NotBlank
	private String oldPassword;
	@NotBlank
	private String newPassword;
}
