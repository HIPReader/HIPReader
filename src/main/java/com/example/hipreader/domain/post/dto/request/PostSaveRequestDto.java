package com.example.hipreader.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostSaveRequestDto {

	@NotBlank(message = "게시물 제목은 반드시 있어야 합니다.")
	private String title;

	@NotBlank(message = "게시물 내용은 반드시 있어야 합니다.")
	private String content;
}
