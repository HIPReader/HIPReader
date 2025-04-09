package com.example.hipreader.domain.post.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdatePostResponseDto {
	private final Long id;
	private final String title;
	private final String content;
	private final String writer;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static UpdatePostResponseDto toDto(Post post) {
		return UpdatePostResponseDto.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.writer(post.getUser().getNickname())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}
}
