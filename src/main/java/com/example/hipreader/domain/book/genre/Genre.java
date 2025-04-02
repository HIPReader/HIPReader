package com.example.hipreader.domain.book.genre;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {

	FICTION("Fiction"),
	NON_FICTION("Non-fiction"),
	MYSTERY("Mystery"),
	FANTASY("Fantasy"),
	SCIENCE_FICTION("Science Fiction"),
	BIOGRAPHY("Biography"),
	HISTORY("History");

	private final String genre;

	@Override
	public String toString() {
		return genre;
	}
}
