package com.example.hipreader.domain.userdiscussion.support;

import lombok.Getter;

@Getter
public class TestResultCollector {
	private long elapsedTime; // 소요 시간(ms)
	private int successCount; // 성공한 신청자 수
	private int failureCount; // 실패한 신청자 수
	private int exceptionCount; // 예외 발생 수
	private int conflictCount; // 낙관적 락 충돌 수

	public void record(long elapsedTime, int successCount, int failureCount, int exceptionCount, int conflictCount) {
		this.elapsedTime = elapsedTime;
		this.successCount = successCount;
		this.failureCount = failureCount;
		this.exceptionCount = exceptionCount;
		this.conflictCount = conflictCount;
	}

	@Override
	public String toString() {
		return "[테스트 결과]\n" +
			"소요 시간: " + elapsedTime + "ms\n" +
			"성공: " + successCount + "명\n" +
			"실패: " + failureCount + "명\n" +
			"예외: " + exceptionCount + "건\n" +
			"충돌: " + conflictCount + "건\n";
	}
}
