package com.example.hipreader.domain.chatmessage.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hipreader.auth.dto.AuthUser;
import com.example.hipreader.domain.chatmessage.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatMessageService chatMessageService;

	@GetMapping("/v1/chat")
	public String enterDiscussionRoom(
		@RequestParam String roomId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		// 로그인 여부 확인
		if (authUser == null) {
			return "redirect:/login.html?roomId=" + roomId;
		}

		Long userId = authUser.getId();
		Long discussionId = Long.parseLong(roomId);

		// 토론방 입장 가능한지 검증
		chatMessageService.validateUserCanJoinDiscussion(userId, discussionId);

		return "redirect:/chat.html?roomId=" + roomId;
	}
}
