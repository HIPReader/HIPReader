package com.example.hipreader.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice(basePackages = "com.example.hipreader.api")
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException e) {
		return createErrorResponse(e.getErrorCode());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException e) {
		return createErrorResponse(e.getErrorCode());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException e) {
		return createErrorResponse(e.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
		log.info(e.getMessage());
		return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Map<String, Object>> handleConflict(ConflictException e) {
		return createErrorResponse(e.getErrorCode());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
		String message = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(fieldError -> fieldError.getDefaultMessage())
			.orElse("잘못된 요청입니다.");

		return createErrorResponse(ErrorCode.INVALID_INPUT_VALUE, message);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(ValidationException e) {
		return createErrorResponse(e.getErrorCode(), e.getMessage());
	}

	private ResponseEntity<Map<String, Object>> createErrorResponse(ErrorCode errorCode) {
		Map<String, Object> response = new HashMap<>();
		response.put("code", errorCode.name()); // 예: "USER_NOT_FOUND"
		response.put("message", errorCode.getMessage());
		response.put("status", errorCode.getStatus().value());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	private ResponseEntity<Map<String, Object>> createErrorResponse(ErrorCode errorCode, String message) {
		Map<String, Object> response = new HashMap<>();
		response.put("code", errorCode.name());
		response.put("message", message);
		response.put("status", errorCode.getStatus().value());
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}
}
