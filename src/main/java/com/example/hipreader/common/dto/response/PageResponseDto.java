package com.example.hipreader.common.dto.response;

import java.util.List;

import com.example.hipreader.domain.book.dto.response.BooksResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private long totalElements;
	private List<T> content;

	public PageResponseDto(Page<T> page) {
		this.pageNumber = page.getNumber();
		this.pageSize = page.getSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.content = page.getContent();

	}
}
