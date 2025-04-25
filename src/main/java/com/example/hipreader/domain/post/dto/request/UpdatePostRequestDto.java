package com.example.hipreader.domain.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePostRequestDto {
	private String title;
	private String content;
}
