package com.example.hipreader.common.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
	private final ErrorCode errorCode;

	public ValidationException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ValidationException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
