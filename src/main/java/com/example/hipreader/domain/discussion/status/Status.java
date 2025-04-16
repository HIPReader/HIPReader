package com.example.hipreader.domain.discussion.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    WAITING("시작 전"),
    ACTIVE("진행 중"),
    CLOSED("종료됨"),
    CANCELLED("취소됨");

    private final String label;

    public String getLabel() {
        return label;
    }
}
