package com.example.hipreader.domain.userbook.dto.request;

import com.example.hipreader.domain.userbook.status.Status;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserBookRequestDto {

	@NotNull
	private Status status;

	@Min(0)
	@NotNull
	private Integer progress;
}
