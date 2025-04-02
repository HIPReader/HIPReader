package com.example.heapreader.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserResponseDto {

	private final Long userId;
	private final String email;
}
