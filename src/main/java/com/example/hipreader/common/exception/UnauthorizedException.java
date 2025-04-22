package com.example.hipreader.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class UnauthorizedException  extends RuntimeException {
  private final ErrorCode errorCode;

  public UnauthorizedException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public HttpStatus getStatus() {
    return errorCode.getStatus();
  }
}
