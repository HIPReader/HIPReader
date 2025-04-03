package com.example.hipreader.domain.book.genre;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Genre {

	ESSAY("수필/에세이"),
	POEM("시"),
	PLAY("희곡"),
	NOVEL("소설"),
	MYSTERY("미스터리"),
	BOOK_ON_HUMANITIES("인문학책"),
	DETECTIVE_STORY("추리"),
	SELF_HELP_BOOK("자기계발서"),
	SCIENTIFIC_BOOK("과학"),
	HEALTH("건강"),
	COOKING("요리"),
	TRAVELING("여행"),
	HISTORY("역사"),
	TEXTBOOK("교과서"),
	WORKBOOK("문제집"),
	CERTIFICATION_BOOK("자격증책"),
	DRAWING_BOOK("드로잉북"),
	COLORING_BOOK("컬러링북"),
	ENCYCLOPEDIA("백과사전"),
	THESIS("논문"),
	NEWSPAPER("신문"),
	MAGAZINE("잡지");

	private final String genreName;

	@Override
	public String toString() {
		return genreName;
	}
}
