package com.example.hipreader.common.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private long totalElements;
	private List<T> content;
}
