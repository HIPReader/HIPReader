package com.example.hipreader.domain.chatmessage.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.chatmessage.entity.ChatMessage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetChatMessageResponseDto {
	private String nickname;
	private String message;
	private LocalDateTime createdAt;

	public static GetChatMessageResponseDto toDto(ChatMessage chatMessage) {
		return GetChatMessageResponseDto.builder()
			.message(chatMessage.getMessage())
			.createdAt(chatMessage.getCreatedAt())
			.nickname(chatMessage.getUser().getNickname())
			.build();
	}
}
