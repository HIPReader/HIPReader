package com.example.hipreader.common.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
	private final ErrorCode errorCode;

	public ConflictException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
