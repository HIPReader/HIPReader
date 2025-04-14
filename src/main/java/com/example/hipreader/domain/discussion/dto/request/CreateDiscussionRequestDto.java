package com.example.hipreader.domain.discussion.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDiscussionRequestDto {

	@NotBlank(message = "토론 주제는 필수입니다.")
	private String topic;

	@NotNull(message = "참여자 수는 필수입니다.")
	private Integer participants;

	@NotNull(message = "토론 예정 시간은 필수입니다.")
	@Future(message = "토론 시간은 현재보다 이후여야 합니다.")
	private LocalDateTime scheduledAt;

	@NotNull(message = "책 ID는 필수입니다.")
	private Long bookId;
}
