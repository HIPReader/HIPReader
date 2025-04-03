package com.example.hipreader.common.exception;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> responseStatusExceptionException(ResponseStatusException ex) {
		return getErrorResponse(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", status.name());
		errorResponse.put("code", status.value());

		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			fieldErrors.put(error.getField(), error.getDefaultMessage())
		);
		errorResponse.put("errors", fieldErrors);
		errorResponse.put("message", "잘못된 요청입니다.");
		return new ResponseEntity<>(errorResponse, status);
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<Map<String, Object>> handleServerException(ServerException ex) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return getErrorResponse(status, ex.getMessage());
	}

	public ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("status", status.name());
		errorResponse.put("code", status.value());
		errorResponse.put("message", message);

		return new ResponseEntity<>(errorResponse, status);
	}
}
