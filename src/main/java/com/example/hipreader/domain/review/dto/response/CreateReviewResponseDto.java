package com.example.hipreader.domain.review.dto.response;

import com.example.hipreader.domain.review.entity.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewResponseDto {

	private Long id;
	private String content;
	private Integer rating;
	private Long userId;
	private Long bookId;

	public static CreateReviewResponseDto toDto(Review review) {
		return CreateReviewResponseDto.builder()
			.id(review.getId())
			.content(review.getContent())
			.rating(review.getRating())
			.userId(review.getUser().getId())
			.bookId(review.getBook().getId())
			.build();
	}
}
