package com.example.hipreader.common.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
  private final ErrorCode errorCode;

  public NotFoundException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
