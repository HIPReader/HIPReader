package com.example.hipreader.domain.user.dto.response;

import com.example.hipreader.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetUserResponseDto {

	private final Long userId;
	private final String email;

	public static GetUserResponseDto toDto(User user) {
		return GetUserResponseDto.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.build();
	}
}
