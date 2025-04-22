package com.example.hipreader.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequestDto {

	@NotBlank(message = "리뷰 내용은 필수입니다.")
	@Size(min = 5, max = 1000, message = "리뷰는 5자 이상 1000자 이하여야 합니다.")
	private String content;

	@NotNull(message = "평점은 필수입니다.")
	@Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
	@Max(value = 5, message = "평점은 5점 이하여야 합니다.")
	private Integer rating;

	@NotNull(message = "사용자 ID는 필수입니다.")
	private Long userId;

	@NotNull(message = "책 ID는 필수입니다.")
	private Long bookId;
}
