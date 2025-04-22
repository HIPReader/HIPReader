package com.example.hipreader.domain.discussion.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.discussion.dto.request.CreateDiscussionRequestDto;
import com.example.hipreader.domain.discussion.dto.request.UpdateDiscussionRequestDto;
import com.example.hipreader.domain.discussion.dto.response.CreateDiscussionResponseDto;
import com.example.hipreader.domain.discussion.dto.response.GetDiscussionResponseDto;
import com.example.hipreader.domain.discussion.dto.response.UpdateDiscussionResponseDto;
import com.example.hipreader.domain.discussion.service.DiscussionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discussions")
public class DiscussionController {

	private final DiscussionService discussionService;

	@PostMapping
	public ResponseEntity<CreateDiscussionResponseDto> createDiscussion(
		@RequestBody @Valid CreateDiscussionRequestDto requestDto,
		@AuthenticationPrincipal AuthUser authUser
	) {
		CreateDiscussionResponseDto createDiscussionResponseDto = discussionService.createDiscussion(requestDto,
			authUser);

		return new ResponseEntity<>(createDiscussionResponseDto, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<GetDiscussionResponseDto>> getDiscussions(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Page<GetDiscussionResponseDto> getDiscussionResponseDto = discussionService.getDiscussions(page, size);

		return new ResponseEntity<>(getDiscussionResponseDto, HttpStatus.OK);
	}

	@GetMapping("/{discussionId}")
	public ResponseEntity<GetDiscussionResponseDto> getDiscussion(
		@PathVariable Long discussionId
	) {
		GetDiscussionResponseDto getDiscussionResponseDto = discussionService.getDiscussion(discussionId);

		return new ResponseEntity<>(getDiscussionResponseDto, HttpStatus.OK);
	}

	@PatchMapping("/{discussionId}")
	public ResponseEntity<UpdateDiscussionResponseDto> updateDiscussion(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody UpdateDiscussionRequestDto updateDiscussionRequestDto,
		@PathVariable Long discussionId
	) {
		UpdateDiscussionResponseDto updateDiscussionResponseDto = discussionService.updateDiscussion(authUser,
			updateDiscussionRequestDto, discussionId);

		return new ResponseEntity<>(updateDiscussionResponseDto, HttpStatus.OK);
	}

	@DeleteMapping("/{discussionId}")
	public ResponseEntity<Void> deleteDiscussion(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long discussionId
	) {
		discussionService.deleteDiscussion(authUser, discussionId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
