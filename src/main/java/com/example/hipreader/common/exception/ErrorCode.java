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
	INVALID_ACCESS_TOKEN("유효하지 않은 AccessToken 입니다.", UNAUTHORIZED),
	NEED_LOGIN("재로그인이 필요합니다.", UNAUTHORIZED),
	INVALID_REFRESH_TOKEN("유효하지 않은 REFRESHTOKEN입니다.", UNAUTHORIZED),

	// 리뷰 관련 예외 코드
	REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다.", NOT_FOUND),
	REVIEW_UNAUTHORIZED("해당 작성자가 아닙니다.", UNAUTHORIZED),
	REVIEW_ALREADY_EXISTS("이미 이 책에 대한 리뷰를 작성하셨습니다.", CONFLICT),
	INVALID_INPUT_VALUE("잘못된 요청입니다.", BAD_REQUEST),

	// 자유게시판 관련 예외 코드
	POST_NOT_FOUND("해당 게시물을 찾을 수 없습니다.", NOT_FOUND),
	POST_UNAUTHORIZED("해당 작성자가 아닙니다.", UNAUTHORIZED),

	// 책 관련 예외코드
	BOOK_DUPLICATION("이 책은 이미 등록되어 있습니다.", CONFLICT),

	// 올해의 책 관련 예외 코드
	BOOK_NOT_PUBLISHED("올해 출판된 책이 없습니다.", NOT_FOUND),
	BOOK_NOT_FOUND("책 정보를 찾을 수 없습니다.", NOT_FOUND),
	SCORE_NOT_FOUND("올해의 책 점수를 찾을 수 없습니다.", NOT_FOUND),

	// 유저 북 관련 예외 코드
	NOT_USER_BOOK("해당 책은 사용자의 책이 아닙니다.", NOT_FOUND),
	INVALID_BOOK_PAGE("페이지 수가 없는 도서는 등록할 수 없습니다.", BAD_REQUEST),

	// 책 상태 관련 예외 코드
	INVALID_STATUS_TRANSITION("TO_READ 상태에서는 READING으로만 변경할 수 있습니다.", BAD_REQUEST),
	INVALID_PROGRESS_FOR_TO_READ("TO_READ 상태에서는 progress를 0으로 유지해야 합니다.", BAD_REQUEST),
	INVALID_PROGRESS_RANGE("progress는 0부터 전체 페이지 수 사이여야 합니다.", BAD_REQUEST),
	ALREADY_FINISHED_BOOK("이미 다 읽은 책입니다.", BAD_REQUEST),

	// 시스템 관련 예외 코드
	INTERNAL_SERVER_ERROR("내부 서버 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	// 토론 관련 예외 코드
	DISCUSSION_NOT_FOUND("해당 토론을 찾을 수 없습니다.", NOT_FOUND),
	APPLICATION_NOT_FOUND("신청 내역을 찾을 수 없습니다.", NOT_FOUND),
	ALREADY_APPLIED("이미 신청한 토론방입니다.", BAD_REQUEST),
	DISCUSSION_NOT_ACTIVE("현재 참여할 수 없는 토론방입니다.", BAD_REQUEST),
	ONLY_CAN_HOST("방장만 승인/거절 할 수 있습니다.", UNAUTHORIZED),
	USER_NOT_APPROVED("토론방 참여 승인이 필요합니다.", UNAUTHORIZED),
	DISCUSSION_FULL("정원이 가득 찼습니다.", CONFLICT),
	INVALID_REQUEST("요청이 올바르지 않습니다.", BAD_REQUEST);

	private final String message;
	private final HttpStatus status;
}
