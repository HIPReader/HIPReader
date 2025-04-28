package com.example.hipreader.domain.userdiscussion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.userdiscussion.dto.request.ApplyUserDiscussionRequestDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApplyUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.ApproveUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.GetUserAppliedDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.GetUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.dto.response.RejectUserDiscussionResponseDto;
import com.example.hipreader.domain.userdiscussion.entity.UserDiscussion;
import com.example.hipreader.domain.userdiscussion.service.UserDiscussionService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserDiscussionController {

	private final UserDiscussionService userDiscussionService;

	// 토론방 신청
	@PostMapping("/v1/userDiscussions/apply")
	public ResponseEntity<ApplyUserDiscussionResponseDto> apply(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody ApplyUserDiscussionRequestDto requestDto
	) {
		ApplyUserDiscussionResponseDto result = userDiscussionService.apply(authUser, requestDto);

		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	// 신청 승인
	@PatchMapping("/v1/userDiscussions/{userDiscussionId}/approve")
	public ResponseEntity<ApproveUserDiscussionResponseDto> approve(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long userDiscussionId
	) {
		ApproveUserDiscussionResponseDto result = userDiscussionService.approve(authUser, userDiscussionId);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// 신청 거부
	@PatchMapping("/v1/userDiscussions/{userDiscussionId}/reject")
	public ResponseEntity<RejectUserDiscussionResponseDto> reject(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long userDiscussionId
	) {
		RejectUserDiscussionResponseDto result = userDiscussionService.reject(authUser, userDiscussionId);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// 토론방별 신청자 목록 조회
	@GetMapping("/v1/userDiscussions/by-discussion/{discussionId}")
	public ResponseEntity<List<GetUserDiscussionResponseDto>> findByDiscussion(
		@PathVariable Long discussionId
	) {
		List<GetUserDiscussionResponseDto> list = userDiscussionService.findByDiscussion(discussionId);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	// 사용자별 신청 내역 조회
	@GetMapping("/v1/userDiscussions/by-user/{userId}")
	public ResponseEntity<List<GetUserAppliedDiscussionResponseDto>> findByUser(
		@PathVariable Long userId
	) {
		List<GetUserAppliedDiscussionResponseDto> list = userDiscussionService.findByUser(userId);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
