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
public class ReadReviewResponseDto {

	private Long id;
	private String content;
	private Integer rating;
	private Long userId;
	private Long bookId;

	public static ReadReviewResponseDto toDto(Review review) {
		return ReadReviewResponseDto.builder()
			.id(review.getId())
			.content(review.getContent())
			.rating(review.getRating())
			.userId(review.getUser().getId())
			.bookId(review.getBook().getId())
			.build();
	}
}
