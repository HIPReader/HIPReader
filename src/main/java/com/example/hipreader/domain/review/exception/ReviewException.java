package com.example.hipreader.domain.review.exception;

import com.example.hipreader.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReviewException extends RuntimeException {

    private final ErrorCode errorCode;

    public ReviewException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
