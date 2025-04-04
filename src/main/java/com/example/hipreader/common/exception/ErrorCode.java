package com.example.hipreader.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 유저 관련 예외 코드
	USER_EMAIL_DUPLICATION("다른 유저와 이메일이 중복됩니다.", CONFLICT),
	USER_NAME_DUPLICATION("다른 유저와 이름이 중복됩니다.", CONFLICT),
	USER_NOT_LOGIN("로그인이 필요합니다. 로그인을 해주세요.", UNAUTHORIZED),
	USER_NOT_FOUND("해당하는 유저를 찾을 수 없습니다.", NOT_FOUND),
	INVALID_PASSWORD("패스워드가 올바르지 않습니다.", BAD_REQUEST),
	INVALID_NEW_PASSWORD_FORMAT("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", BAD_REQUEST),
	PASSWORD_SAME_AS_OLD("이전 패스워드와 동일할 수 없습니다.", BAD_REQUEST),
	USER_ACCESS_DENIED("사용자가 접근할 수 있는 권한이 없습니다.", FORBIDDEN),
	USER_ROLE_SAME_AS_OLD("이전 역활과 동일할 수 없습니다.", BAD_REQUEST),
	INVALID_USER_ROLE("유효하지 않는 role 입니다.", BAD_REQUEST),

	// 토큰 관련 예외 코드
	TOKEN_NOT_FOUND("해당 토큰을 찾을 수 없습니다.", NOT_FOUND),
	INVALID_TOKEN("유효하지 않은 토큰입니다.", UNAUTHORIZED),
	EXPIRED_TOKEN("만료된 토큰입니다.", UNAUTHORIZED),
	TOKEN_DUPLICATED("중복된 토큰입니다.", CONFLICT),
	INVALID_ACCESS_TOKEN("유효하지 않은 Access Token입니다.", UNAUTHORIZED),
	NEED_LOGIN("재로그인이 필요합니다.",UNAUTHORIZED),

	// 리뷰 관련 예외 코드
	REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다.", NOT_FOUND),

	// 자유게시판 관련 예외 코드
	POST_NOT_FOUND("해당 게시물을 찾을 수 없습니다.", NOT_FOUND);

	private final String message;
	private final HttpStatus status;
}
