package com.example.hipreader.domain.post.dto.response;

import java.time.LocalDateTime;

import com.example.hipreader.domain.post.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetPostResponseDto {
	private final String title;
	private final String content;
	private final String writer;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static PostGetResponseDto toDto(Post post) {
		return PostGetResponseDto.builder()
			.title(post.getTitle())
			.content(post.getContent())
			.writer(post.getUser().getNickname())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}
}
