package com.example.hipreader.domain.post.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SavePostResponseDto {
	private final Long id;
	private final String title;
	private final String content;
	private final String writer;
	private final Integer viewCount;
	private final Integer likeCount;
	private final LocalDateTime createdAt;
}
