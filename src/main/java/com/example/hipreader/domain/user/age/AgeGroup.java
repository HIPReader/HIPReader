package com.example.hipreader.domain.user.age;

import lombok.Getter;

public enum AgeGroup {
	TEENS(10, 19),
	TWENTIES(20, 29),
	THIRTIES(30, 39),
	FORTIES(40, 49);

	@Getter
	private final int minAge;

	@Getter
	private final int maxAge;

	AgeGroup(int minAge, int maxAge) {
		this.minAge = minAge;
		this.maxAge = maxAge;
	}

	public static AgeGroup fromAge(int age) {
		for (AgeGroup group : values()) {
			if (age >= group.minAge && age <= group.maxAge) {
				return group;
			}
		}
		throw new IllegalArgumentException("입력하신 나이(" + age + ")에 해당하는 연령대가 없습니다. 10세 이상 49세 이하의 나이를 입력해 주세요.");
	}
}
